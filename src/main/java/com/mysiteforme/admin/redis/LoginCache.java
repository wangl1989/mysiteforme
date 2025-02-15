/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 15:08:01
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 12:35:40
 * @ Description: 用户登录缓存操作
 */

package com.mysiteforme.admin.redis;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.security.RequestWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.entity.VO.PermissionVO;
import com.mysiteforme.admin.entity.VO.RoleVO;
import com.mysiteforme.admin.entity.VO.UserLoginFail;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginCache {

    private final  RedisUtils redisUtils;
 
    public LoginCache(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 登录失败提示信息
     */
    private final static String ERROR_MSG = MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED);
    /**
     * 登录失败错误代码
     */
    private final static int ERROR_CODE = ResultCode.LOGIN_ERROR;

    /**
     * 登录失败，用户被锁定Redis操作
     * @param username 用户名
     * @return Result准备输出前端对象
     * @throws MyException 自定义异常
     */
    public Result loginFailData(String username) throws MyException{
        // 判定是否有用户登录相关的key
        if (redisUtils.hasKey(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + username)) {
            UserLoginFail userLoginFail = redisUtils.get(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + username, UserLoginFail.class);
            if(userLoginFail == null){
                // 这里是类型转换异常
                log.error("用户登录reids类型转换异常：reidskey：{}",RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + username);
                throw MyException.builder().systemError(MessageConstants.Exception.EXCEPTION_LOGIN_LIMIT).build();
            }
            if(userLoginFail.getLoginCount() >= Constants.ALLOW_USER_LOGIN_FAIL_COUNT) {
                // 用户登录失败次数超过限制，返回锁定用户,这里用Redis来控制次数，不用通过数据库
                return Result.error(ResultCode.LOGIN_FAILED_LIMIT,MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED_LIMIT));
            }else{
                userLoginFail.setLoginCount(userLoginFail.getLoginCount()+1);
            }
            redisUtils.set(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + username, userLoginFail,Constants.USER_WAIT_TO_LOGIN,TimeUnit.MINUTES);
        } else {
            redisUtils.set(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + username, UserLoginFail.builder().loginName(username).loginTime(System.currentTimeMillis()).loginCount(1).build());
        }
        return Result.error(ERROR_CODE,ERROR_MSG);
    }

    /**
     * 登录成功，删除验证登录失败缓存
     * @param user 用户对象
     */
    public void loginSuccess(MyUserDetails user, HttpServletResponse response) throws ServletException,IOException{
        
        //  获取随机token 并存到Redis中
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //  这里已经登录成功，删除登录尝试错误对象的缓存
        redisUtils.del(RedisConstants.USER_LOGIN_FAIL_CACHE_KEY + user.getLoginName());
        // 缓存用户信息 userVO
        redisUtils.set(RedisConstants.LOGIN_CACHE_KEY + token, user, Constants.USER_LOGIN_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        // 将token返回给前端
        ApiToolUtil.returnSystemDate(Result.success(MessageUtil.getMessage(MessageConstants.SUCCESS),token), response);
    }

    /**
     * 检查token是否有效
     * @param authHeader 请求头中的token
     * @return 是否有效
     */
    public Boolean checkToken(String authHeader, HttpServletRequest request){
        // 提取token值
        String token = authHeader.substring(Constants.GRANT_TYPE.length());
        if (StringUtils.isBlank(token)) {
            return false;
        }
        //  通过token值从缓存中取用户信息
        MyUserDetails userDetails = redisUtils.get(RedisConstants.LOGIN_CACHE_KEY + token, MyUserDetails.class);
        //  转换JSON对象

        //JSONObject userJsonObject = JSON.parseObject(userJson);
        //  判断是否空值
        if (ObjectUtils.isEmpty(userDetails)) {
            return false;
        }
        //  MyUserDetails对象放到上下文中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return true;
    }

    /**
     * 登出成功，删除token缓存
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String authHeader = request.getHeader(Constants.AUTHORIZATION);
        String authToken = authHeader.substring(Constants.GRANT_TYPE.length());
        MyUserDetails userDetails = redisUtils.get(RedisConstants.LOGIN_CACHE_KEY + authToken, MyUserDetails.class);
        if (ObjectUtils.isEmpty(userDetails)) {
            log.error("用户登出:类型转换失败：token:{},authHeader:{}",authToken,authHeader);
            throw MyException.builder().systemError(MessageConstants.Exception.EXCEPTION_LOGIN_OUT).build();
        }
        // 用户登出，移除token对应的缓存
        redisUtils.del(RedisConstants.LOGIN_CACHE_KEY + authToken);

        ApiToolUtil.returnSystemDate(Result.success(MessageUtil.getMessage(MessageConstants.User.USER_LOGOUT_SUCCESS)), response);
    }

    /**
     * 验证码校验: 前端要把code和key传过来
     * code: 用户输入的验证码
     * key: 生成验证码时生成的key(也就是token)
     */
    public void validateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String code = getCaptchaFromRequest(request);
        String key = request.getHeader(Constants.CAPTCHA_TOKEN);
        // 验证码为空验证
        if (StringUtils.isBlank(code)) {
            ApiToolUtil.returnSystemDate(Result.paramError(MessageUtil.getMessage(MessageConstants.User.USER_CAPTCHA_NULL)), response);
        }
        // 验证码token为空验证
        if (StringUtils.isBlank(key)) {
            ApiToolUtil.returnSystemDate(Result.paramError(MessageUtil.getMessage(MessageConstants.User.USER_CAPTCHA_ERROR)), response);
        }
        // 如果缓存中没有验证码，则验证码已过期或不存在
        if (!redisUtils.hasKey(RedisConstants.USER_CAPTCHA_CACHE_KEY + key)) {
            ApiToolUtil.returnSystemDate(Result.paramError(MessageUtil.getMessage(MessageConstants.User.USER_CAPTCHA_ERROR)), response);
        }
        String cacheCode = redisUtils.get(RedisConstants.USER_CAPTCHA_CACHE_KEY + key, String.class);
        
        if (!code.equals(cacheCode)) {
            ApiToolUtil.returnSystemDate(Result.paramError(MessageUtil.getMessage(MessageConstants.User.USER_CAPTCHA_ERROR)), response);
        }
        
        // 验证通过后删除验证码
        redisUtils.del(RedisConstants.USER_CAPTCHA_CACHE_KEY + key);
    }

    private String getCaptchaFromRequest(HttpServletRequest request) throws IOException {
        // 因为是JSON请求，需要从请求体中读取验证码
        String body = request.getReader().lines().collect(Collectors.joining());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(body);
        return node.get("captcha").asText();
    }
}
