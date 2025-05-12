/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 00:30:50
 * @ Description: 用户登录相关的controller
 */

package com.mysiteforme.admin.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mysiteforme.admin.entity.request.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.DTO.TokenValidationResult;
import com.mysiteforme.admin.entity.VO.TokenInfoVO;
import com.mysiteforme.admin.redis.JwtService;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.redis.TokenStorageService;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;
import com.mysiteforme.admin.util.TokenErrorType;
import com.mysiteforme.admin.util.ToolUtil;
import com.mysiteforme.admin.util.VerifyCodeUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录控制器
 * 处理用户登录、登出等认证相关操作
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

	private final RedisUtils redisUtils;

	private final JwtService jwtService;

	private final TokenStorageService tokenStorageService;

	private final UserDetailsService userDetailsService;

	/**
	 * 生成验证码
	 * @param request request对象
	 */
	@GetMapping("/genCaptcha")
	public Result genCaptcha(HttpServletRequest request) throws IOException {
		String deviceId = request.getHeader(Constants.DEVICE_ID);
		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_ALL_MIXED, 4, null);
		//将验证码放到缓存里面REDIS,验证码有效期为5分钟
		redisUtils.set(RedisConstants.USER_CAPTCHA_CACHE_KEY+deviceId, verifyCode, Constants.USER_CAPTCHA_CACHE_EXPIRE_TIME,TimeUnit.MINUTES);
		log.debug("本次生成的验证码为[{}],已存放到Redis中,redis的key值为{}",verifyCode,RedisConstants.USER_CAPTCHA_CACHE_KEY+deviceId);
		BufferedImage bufferedImage = VerifyCodeUtil.generateImageCode(verifyCode, 116, 36, 5, true, new Color(249,205,173), null, null);
		Map<String,String> map = Maps.newHashMap();
		map.put("image", ToolUtil.getBase64FromImage(bufferedImage));
		return Result.success(map);
	}

	@PostMapping("/error")
	@GetMapping("/error")
	public Result handleError(Exception exception){
        // 根据状态码和异常类型返回合适的响应
        log.error("系统错误，请联系管理员:{}", exception.getMessage());
		return Result.error(ResultCode.INTERNAL_ERROR, MessageConstants.System.SYSTEM_ERROR);
	}


	@PostMapping("/api/auth/refresh")
	public Result refreshToken(@RequestBody RefreshTokenRequest request,
										  HttpServletRequest httpRequest) {
		try {

			// 获取设备ID
			String deviceId = httpRequest.getHeader(Constants.DEVICE_ID);
			if(StringUtils.isBlank(deviceId)){
				return Result.paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED);
			}

			//获取刷新令牌
			String refreshToken = request.getRefreshToken();

			if(StringUtils.isBlank(refreshToken)){
				return Result.paramMsgError(MessageConstants.JwtToken.JWT_REFRESH_TOKEN_NULL);
			}

			if(!jwtService.validateDeviceId(refreshToken,deviceId)){
				log.error("jwt验证：设备ID无效————>{}",deviceId);
				return Result.refreshTokenInvalidError(MessageConstants.JwtToken.JWT_DEVICE_ID_INVALID);
			}

			TokenValidationResult tokenValidationResult = jwtService.validateRefreshToken(refreshToken);
			if (!tokenValidationResult.isValid()) {
				return Result.refreshTokenInvalidError(tokenValidationResult.getErrorMessage());
			}

			String username = jwtService.extractUsername(refreshToken);
			MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(username);
			userDetails.setDeviceId(deviceId);

			// 验证存储的refresh token
			if (!tokenStorageService.validateRefreshToken(username, refreshToken,deviceId)) {
				return Result.refreshTokenInvalidError(MessageConstants.JwtToken.JWT_REFRESH_TOKEN_REVOKED);
			}

			// 生成新的token对
			String newAccessToken = jwtService.generateToken(userDetails);
			String newRefreshToken = jwtService.generateRefreshToken(userDetails);

			// 存储新的token
			tokenStorageService.storeAccessToken(username, newAccessToken,deviceId);
			tokenStorageService.storeRefreshToken(username, newRefreshToken,deviceId);


			return Result.success(TokenInfoVO.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build());

		} catch (UsernameNotFoundException e) {
			log.error("刷新token出现异常:", e);
			return Result.refreshTokenInvalidError(MessageConstants.JwtToken.JWT_REFRESH_TOKEN_FAILED);
		}
	}

	/**
	 * 校验token是否有效
	 * @return Result对象
	 */
	@GetMapping("/api/auth/validate")
	public Result tokenValidate(HttpServletRequest request){
		String deviceId = request.getHeader(Constants.DEVICE_ID);
        // 校验设备ID
        if (StringUtils.isBlank(deviceId)) {
			return Result.paramMsgError(MessageConstants.User.DEVICE_ID_REQUIRED);
        }
        String authHeader = request.getHeader(Constants.AUTHORIZATION);
        // 校验head是否有权限头
        if (StringUtils.isBlank(authHeader)) {
			return Result.paramMsgError(MessageConstants.Validate.VALIDATE_HEADER_AUTH_ERROR);
        }
        // 提取token值
        String token = authHeader.substring(Constants.GRANT_TYPE.length());
        if (StringUtils.isBlank(token)) {
			return Result.paramMsgError(MessageConstants.JwtToken.JWT_TOKEN_NULL);
        }
		try {
			String username = jwtService.extractUsername(token);
			// 校验token是否合法
			TokenValidationResult tokenValidationResult = jwtService.validateToken(token);
			if (!tokenValidationResult.isValid()) {
				if(tokenValidationResult.getErrorType() == TokenErrorType.EXPIRED){
					return Result.tokenInvalidError(MessageConstants.JwtToken.JWT_TOKEN_EXPIRED);
				}else{
					return Result.tokenInvalidError(MessageConstants.JwtToken.JWT_TOKEN_INVALID);
				}
			}
			if(!tokenStorageService.validateAccessToken(username, token, deviceId)){
				return Result.tokenInvalidError(MessageConstants.JwtToken.JWT_TOKEN_REVOKED);
			}
		} catch (Exception e) {
			log.error("校验token出现异常:", e);
			return Result.tokenInvalidError(MessageConstants.JwtToken.JWT_TOKEN_INVALID);
		}
		return Result.success();
	}



}