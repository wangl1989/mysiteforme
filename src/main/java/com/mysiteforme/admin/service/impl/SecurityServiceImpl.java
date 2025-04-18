/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 15:08:01
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 00:18:24
 * @ Description: 用户登录缓存操作
 */

package com.mysiteforme.admin.service.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mysiteforme.admin.entity.VO.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.entity.DTO.GlobalHeadParam;
import com.mysiteforme.admin.entity.DTO.TokenValidationResult;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.JwtService;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.redis.TokenStorageService;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.service.SecurityService;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;
import com.mysiteforme.admin.util.TokenErrorType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final RedisUtils redisUtils;

    private final JwtService jwtService;

    private final TokenStorageService tokenStorageService;

    private final UserDetailsService userDetailsService;

    private final UserDeviceService userDeviceService;

    private final PermissionService permissionService;

    private final ApiToolUtil apiToolUtil;

    private final ObjectMapper objectMapper;
 
    /**
     * 登录失败提示信息
     */
    private final static String ERROR_MSG = MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED);
    /**
     * 登录失败错误代码
     */
    private final static int ERROR_CODE = ResultCode.LOGIN_ERROR;
    private final UserServiceImpl userService;

    /**
     * 登录失败，用户被锁定Redis操作
     * @param request request对象
     * @return Result准备输出前端对象
     */
    @Override
    public Result loginFailData(HttpServletRequest request, HttpServletResponse response){
        String deviceId = request.getHeader(Constants.DEVICE_ID);
        // 如果没有设备ID，说明是非法请求（因为验证码阶段已经生成过设备ID）
        if (StringUtils.isBlank(deviceId)) {
            return Result.paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED);
        }
        // 判定是否有用户登录相关的key
        if (redisUtils.hasKey(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId)) {
            UserLoginFail userLoginFail = redisUtils.get(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId, UserLoginFail.class);
            if(userLoginFail == null){
                // 这里是类型转换异常
                log.error("用户登录redis类型转换异常：RedisKey：{}",RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId);
                return Result.error(ResultCode.INTERNAL_ERROR,MessageConstants.System.SYSTEM_ERROR);
            }
            if(userLoginFail.getLoginCount() >= Constants.ALLOW_USER_LOGIN_FAIL_COUNT) {
                // 用户登录失败次数超过限制，返回锁定用户,这里用Redis来控制次数，不用通过数据库
                return Result.error(ResultCode.LOGIN_FAILED_LIMIT,MessageConstants.User.USER_LOGIN_FAILED_LIMIT);
            }else{
                userLoginFail.setLoginCount(userLoginFail.getLoginCount()+1);
            }
            redisUtils.set(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId, userLoginFail,Constants.USER_WAIT_TO_LOGIN,TimeUnit.MINUTES);
        } else {
            redisUtils.set(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId, UserLoginFail.builder().loginName(deviceId).loginTime(System.currentTimeMillis()).loginCount(1).build());
        }
        return Result.error(ERROR_CODE,ERROR_MSG);
    }

    /**
     * 登录成功，删除验证登录失败缓存
     * @param user 用户对象
     */
    @Override
    public void loginSuccess(MyUserDetails user, HttpServletRequest request,HttpServletResponse response) {
        // 获取设备ID
        String deviceId = request.getHeader(Constants.DEVICE_ID);
        // 如果没有设备ID，说明是非法请求（因为验证码阶段已经生成过设备ID）
        if (StringUtils.isBlank(deviceId)) {
            log.error("用户设备ID为空，用户名：{}",user.getUsername());
            throw MyException.builder().paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
        }

        // 1. 获取设备信息
        UserDevice deviceInfo = userDeviceService.extractDeviceInfo(user.getUsername(),request);
        deviceInfo.setDeviceId(deviceId);
        user.setDeviceId(deviceId);
        // 生成访问令牌和刷新令牌
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        //  这里已经登录成功，删除登录尝试错误对象的缓存
        redisUtils.del(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + deviceId);
        // 在Redis中换成访问令牌和刷新令牌
        tokenStorageService.storeAccessToken(user.getLoginName(), accessToken, deviceId);
        tokenStorageService.storeRefreshToken(user.getLoginName(), refreshToken, deviceId);

        // 4. 更新设备信息
        userDeviceService.handleDeviceLogin(user.getLoginName(), deviceInfo);

        // 将token返回给前端
        apiToolUtil.returnSystemDate(Result.success(TokenInfoVO.builder().accessToken(accessToken).refreshToken(refreshToken).deviceId(deviceId).build()),request, response);
    }

    /**
     * 检查token是否有效
     * @return 是否有效
     */
    @Override
    public Boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        GlobalHeadParam globalHeadParam = checkCommonParam(request);
        // 1. 验证token是否合法
        TokenValidationResult tokenValidationResult = jwtService.validateToken(globalHeadParam.getToken());
        if (!tokenValidationResult.isValid()) {
            log.error("jwt验证：token无效————>{}",globalHeadParam.getToken());
            if(tokenValidationResult.getErrorType() == TokenErrorType.EXPIRED){
                throw MyException.builder().tokenExpired().build();
            }else{
                throw MyException.builder().tokenError(tokenValidationResult.getErrorMessage()).build();
            }
        }
        // 2. 校验设备ID是否匹配token
        if(!jwtService.validateDeviceId(globalHeadParam.getToken(),globalHeadParam.getDeviceId())){
            log.error("jwt验证：设备ID无效————>{}",globalHeadParam.getDeviceId());
            throw MyException.builder().tokenError(MessageConstants.JwtToken.JWT_DEVICE_ID_INVALID).build();
        }

        // 2. 检查token是否在黑名单中（可选，如果需要支持token注销功能）
        if (redisUtils.isTokenBlacklisted(globalHeadParam.getToken())) {
            log.error("jwt验证：token被加入到黑名单中————>{}",globalHeadParam.getToken());
            throw MyException.builder().tokenError(MessageConstants.JwtToken.JWT_TOKEN_HAS_BEEN_INVALIDATED).build();
        }
        // 3.校验token是否过期
        if (jwtService.isTokenExpired(globalHeadParam.getToken())) {
            throw MyException.builder().tokenExpired().build();
        }
        String username = jwtService.extractUsername(globalHeadParam.getToken());
        // 4. 验证Redis里存储的token是否被注销(jwt里已经校验过token是否合法且没有过期，但redis中的token可能被注销)
        if(!tokenStorageService.validateAccessToken(username,globalHeadParam.getToken(),globalHeadParam.getDeviceId())){
            log.error("Redis验证：token无效————>{}",globalHeadParam.getToken());
            throw MyException.builder().tokenError(MessageConstants.JwtToken.JWT_TOKEN_REVOKED).build();
        }
        // 5. 通过用户名获取用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //  判断是否空值
        if (ObjectUtils.isEmpty(userDetails)) {
            return false;
        }
        //  MyUserDetails对象放到上下文中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return true;
    }

    /**
     * 登出成功，删除token缓存
     * @param request 请求对象
     * @param response 响应对象
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response){
        GlobalHeadParam globalHeadParam = checkCommonParam(request);
        String username = jwtService.extractUsername(globalHeadParam.getToken());
        String deviceId = jwtService.extractDeviceId(globalHeadParam.getToken());
        // 删除该设备的访问令牌和刷新令牌
        redisUtils.del(RedisConstants.ACCESS_TOKEN_PREFIX + username + ":" + deviceId);
        redisUtils.del(RedisConstants.REFRESH_TOKEN_PREFIX + username + ":" + deviceId);

        apiToolUtil.returnSystemDate(Result.success(MessageUtil.getMessage(MessageConstants.User.USER_LOGOUT_SUCCESS)),request,response);
    }

    /**
     * 验证码校验: 前端要把code和key传过来
     * code: 用户输入的验证码
     * key: 生成验证码时生成的key(也就是token)
     * Device-Id: 用于识别用户的设备。
     */
    @Override
    public void validateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = getCaptchaFromRequest(request);
        String key = request.getHeader(Constants.CAPTCHA_TOKEN);
        String deviceId = request.getHeader(Constants.DEVICE_ID);
        // 验证码为空验证
        if (StringUtils.isBlank(code)) {
            throw MyException.builder().paramMsgError(MessageConstants.User.USER_CAPTCHA_NULL).build();
        }
        // 验证码token为空验证
        if (StringUtils.isBlank(key)) {
            throw MyException.builder().paramMsgError(MessageConstants.User.USER_CAPTCHA_TOKEN_NULL).build();
        }
        // 校验设备ID
        if (StringUtils.isBlank(deviceId)) {
            throw MyException.builder().paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
        }
        // 如果缓存中没有验证码，则验证码已过期或不存在
        if (!redisUtils.hasKey(RedisConstants.USER_CAPTCHA_CACHE_KEY + key)) {
            throw MyException.builder().paramMsgError(MessageConstants.User.USER_CAPTCHA_ERROR).build();
        }
        String cacheCode = redisUtils.get(RedisConstants.USER_CAPTCHA_CACHE_KEY + key, String.class);
        
        if (!code.equalsIgnoreCase(cacheCode)) {
            throw MyException.builder().businessError(MessageConstants.User.USER_CAPTCHA_ERROR).build();
        }
        // 验证通过后删除验证码
        redisUtils.del(RedisConstants.USER_CAPTCHA_CACHE_KEY + key);
    }

    private String getCaptchaFromRequest(HttpServletRequest request) throws IOException {
        // 因为是JSON请求，需要从请求体中读取验证码
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonNode node = objectMapper.readTree(body);
        return node.get(Constants.CAPTCHA).asText();
    }

    @Override
    public boolean checkPermission(HttpServletRequest request){
        // 获取当前请求信息
        String method = request.getMethod();
        String path = request.getRequestURI();
        // 如果请求方法或请求路径为空，则返回false
        if (StringUtils.isBlank(method) || StringUtils.isBlank(path)) {
            return false;
        }

        // 获取接口信息
        PermissionApiVO permissionApiVO = permissionService.getApiByUrlAndMethod(path, method);
        if(permissionApiVO == null){
            // 接口尚未注册
            throw MyException.builder().paramMsgError(MessageConstants.Permission.API_NOT_REGISTERED).build();
        }
        GlobalHeadParam globalHeadParam = checkCommonParam(request);
        // 获取当前登录用户的权限对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()){
            return false;
        }
        // 如果是公共接口.直接放行
        if(permissionApiVO.getCommon()){
            return true;
        }
        String username = jwtService.extractUsername(globalHeadParam.getToken());
        UserVO userDetails = userService.findUserByLoginNameDetails(username);
        Set<PermissionVO> permissions = userDetails.getPermissions();
        for (PermissionVO permission : permissions) {
            PermissionApiVO permissionApi = permission.getApi();
            if(!ObjectUtils.isEmpty(permissionApi)) {
                if (path.equals(permissionApi.getApiUrl()) && method.equals(permissionApi.getHttpMethod())) {
                    return true;
                }
            }

        }
        // 如果以上条件都不满足，则返回false
        return false;   
    }

    @Override
    public GlobalHeadParam checkCommonParam(HttpServletRequest request){
        GlobalHeadParam param = new GlobalHeadParam(); 
        String authHeader = request.getHeader(Constants.AUTHORIZATION);
        String deviceId = request.getHeader(Constants.DEVICE_ID);
        if (StringUtils.isBlank(deviceId)) {
            throw MyException.builder().paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
        }
        param.setDeviceId(deviceId);
        // 校验head是否有权限头
        if (StringUtils.isBlank(authHeader)) {
            throw MyException.builder().paramMsgError(MessageConstants.Validate.VALIDATE_HEADER_AUTH_ERROR).build();
        }
        param.setAuthHeader(authHeader);
        // 提取token值
        String token = authHeader.substring(Constants.GRANT_TYPE.length());
        if (StringUtils.isBlank(token)) {
            throw MyException.builder().paramMsgError(MessageConstants.JwtToken.JWT_TOKEN_NULL).build();
        }
        param.setToken(token);  
        return param; 
    }
}
