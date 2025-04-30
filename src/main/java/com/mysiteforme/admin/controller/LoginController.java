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
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.service.UserService;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

	private final UserService userService;

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String sendFrom;

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

	/**
	 * 发送邮件
	 * @param request 邮件参数对象
	 * @return 返回结果
	 */
	@PostMapping("/register/sendEmail")
	public Result sendEmail(@RequestBody @Valid SendEmailRequest request) {
		// 检测这个邮箱是否被注册过
		if(userService.userCounByEmail(request.getEmail(),null)>0){
			return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
		}
		if(redisUtils.hasKey(RedisConstants.EMAIL_VALIDATE_SUCCESS)){
			Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_VALIDATE_SUCCESS);
			if(emails.contains(request.getEmail())){
				return Result.success().setCode(ResultCode.CHECK_EMAIL_SUCCESS);
			}
		}
		SimpleMailMessage message = new SimpleMailMessage();
		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_NUM_ONLY, 6, null);
		message.setFrom(sendFrom);
		message.setTo(request.getEmail());
		message.setSubject(Constants.DEFAULT_EMAIL_CHECK_REGISTE_SUBJECT);
		message.setText(String.format(Constants.DEFAULT_EMAIL_REGIST_CONTENT,verifyCode));
		message.setSentDate(new Date());
		try {
			mailSender.send(message);
			redisUtils.set(RedisConstants.USER_EMAIL_KEY+request.getEmail(), verifyCode, Constants.USER_EMAIL_CACHE_EXPIRE_TIME,TimeUnit.MINUTES);
			return Result.success();
		} catch (MailException e) {
			log.error("注册新账户发送邮件失败: {}", e.getMessage(), e);
			return Result.businessMsgError(MessageConstants.User.EMAIL_SEND_FAILED,e.getMessage());
		}
	}

	/**
	 * 验证邮箱验证码
	 */
	@PostMapping("/register/checkEmail")
	public Result checkEmail(@RequestBody @Valid CheckEmailRequest request){
		if(userService.userCounByEmail(request.getEmail(),null)>0){
			return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
		}
		if(!redisUtils.hasKey(RedisConstants.USER_EMAIL_KEY+request.getEmail())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
		}
		String code = redisUtils.get(RedisConstants.USER_EMAIL_KEY+request.getEmail(),String.class);
		if(!code.equalsIgnoreCase(request.getCode())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
		}
		// 清除邮箱验证缓存
		redisUtils.del(RedisConstants.USER_EMAIL_KEY+request.getEmail());
		// 设置验证成功的邮箱
		redisUtils.sSet(RedisConstants.EMAIL_VALIDATE_SUCCESS,Constants.CHECK_EMAIL_SUCCESS_TIME,TimeUnit.HOURS,request.getEmail());
		return Result.success();
	}

	@PostMapping("/register/user")
	public Result registerUser(@RequestBody @Valid RegisterUserRequest request){
		if(userService.userCounByEmail(request.getEmail(),null)>0){
			return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
		}
		if(!redisUtils.hasKey(RedisConstants.EMAIL_VALIDATE_SUCCESS)){
			return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
		}
		Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_VALIDATE_SUCCESS);
		if(!emails.contains(request.getEmail())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
		}
		if(userService.userCounByLoginName(request.getLoginName(),null)>0){
			return Result.businessMsgError(MessageConstants.User.LOGIN_NAME_HAS_EXIST);
		}
		User user = new User();
		BeanUtils.copyProperties(request,user);
		user.setNickName(String.format(Constants.DEFAULT_USER_NICK_NAME,user.getLoginName()));
		UserVO userVO = userService.saveUser(user);
		if(userVO != null && userVO.getId() != null) {
			// 清除验证key
			redisUtils.sRemove(RedisConstants.EMAIL_VALIDATE_SUCCESS, request.getEmail());
		} else {
			return Result.businessMsgError(MessageConstants.User.REGISTER_FAILED);
		}
		return Result.success();
	}

	/**
	 * 发送忘记密码验证码
	 * @param request 参数对象
	 * @return result返回结果
	 */
	@PostMapping("/register/forgetPassword")
	public Result forgetPassword(@RequestBody @Valid ForgetPasswordRequest request){
		User user = userService.getUserByEmail(request.getEmail());
		if(user == null){
			return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
		}
		if(user.getDelFlag()){
			return Result.businessMsgError(MessageConstants.User.USER_DISABLED);
		}
		SimpleMailMessage message = new SimpleMailMessage();
		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_NUM_ONLY, 6, null);
		message.setFrom(sendFrom);
		message.setTo(request.getEmail());
		message.setSubject(Constants.DEFAULT_EMAIL_REST_PASSWORD_SUBJECT);
		message.setText(String.format(Constants.DEFAULT_EMAIL_REST_PASSWORD_CONTENT,verifyCode));
		message.setSentDate(new Date());
		try {
			mailSender.send(message);
			redisUtils.set(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail(), verifyCode, Constants.USER_EMAIL_CACHE_EXPIRE_TIME,TimeUnit.MINUTES);
			return Result.success();
		} catch (MailException e) {
			log.error("忘记密码发送邮件失败: {}", e.getMessage(), e);
			return Result.businessMsgError(MessageConstants.User.EMAIL_SEND_FAILED,e.getMessage());
		}
	}

	/**
	 * 验证发送的验证码
	 * @param request 验证码对象
	 * @return 返回结果
	 */
	@PostMapping("/register/checkRestPasswordEmail")
	public Result checkRestPassowrdEmail(@RequestBody @Valid CheckRestPasswordEmailRequest request){
		if(!redisUtils.hasKey(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
		}
		String code = redisUtils.get(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail(),String.class);
		if(!code.equalsIgnoreCase(request.getCode())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
		}
		// 清除邮箱验证缓存
		redisUtils.del(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail());
		// 设置验证成功的邮箱
		redisUtils.sSet(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS,Constants.CHECK_EMAIL_SUCCESS_TIME,TimeUnit.HOURS,request.getEmail());
		return Result.success();
	}

	/**
	 * 重置密码
	 * @param request 重置密码对象
	 * @return Result返回对象
	 */
	@PutMapping("/register/resetPassword")
	public Result resetPassword(@RequestBody @Valid ResetPasswordRequest request){
		// 是否有重置密码成功的邮箱key
		if(!redisUtils.hasKey(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS)){
			return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
		}
		// 重置密码成功的邮箱key对应的集合中是否有当前email
		Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS);
		if(!emails.contains(request.getEmail())){
			return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
		}
		// 根据email获取用户对象
		User user = userService.getUserByEmail(request.getEmail());
		if(user == null){
			return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
		}
		if(user.getDelFlag() || user.getLocked()){
			return Result.businessMsgError(MessageConstants.User.USER_DISABLED);
		}
		// 设置用户输入的新密码
		user.setPassword(request.getPassword());
		userService.emailChangePassword(user);
		// 清除邮箱验证缓存
		redisUtils.sRemove(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS, request.getEmail());
		return Result.success();
	}



}