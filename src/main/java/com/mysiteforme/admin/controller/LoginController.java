/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 12:37:36
 * @ Description: 用户登录相关的controller
 */

package com.mysiteforme.admin.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.mysiteforme.admin.entity.DTO.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ToolUtil;
import com.mysiteforme.admin.util.VerifyCodeUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/**
 * 登录控制器
 * 处理用户登录、登出等认证相关操作
 */
@Slf4j
@RestController
public class LoginController extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	 
    public LoginController(UserService userService,
						   UserCacheService userCacheService,
						   MenuService menuService,
						   RedisUtils redisUtils) {
        this.userService = userService;
        this.userCacheService = userCacheService;
        this.menuService = menuService;
		this.redisUtils = redisUtils;
    }

	
//	/**
//	 * 用户登录
//	 * @param userDTO 用户登录信息
//	 */
	@PostMapping("/login")
	public Result loginMain(@RequestBody UserDTO userDTO) {
		log.info("=================用户登录===================");
		try {
			// 添加日志
			log.info("用户尝试登录：{}", userDTO.getUsername());

			UsernamePasswordAuthenticationToken token =
					new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());

			Authentication authentication = authenticationManager.authenticate(token);
			// 如果认证成功，这里不会抛出异常

			log.info("用户登录成功：{}", authentication.getName());
			// ... 后续处理

		} catch (AuthenticationException e) {
			log.error("用户登录失败：{}", e.getMessage(), e);
			// ... 异常处理
		}
		return Result.success();
	}
	

	/**
	 * 生成验证码
	 * @param response HTTP响应对象
	 */
	@GetMapping("/genCaptcha")
	public Result genCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = request.getHeader(Constants.CAPTCHA_TOKEN);
		if (StringUtils.isBlank(token)) {
			// 如果从头部信息中没获取到验证码koen，则新建一个token，如果拿到了就用原来的
			token = UUID.randomUUID().toString().replaceAll("-", "");
		}
		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_ALL_MIXED, 4, null);
		//将验证码放到缓存里面REDIS,验证码有效期为5分钟
		redisUtils.set(RedisConstants.USER_CAPTCHA_CACHE_KEY+token, verifyCode, Constants.USER_CAPTCHA_CACHE_EXPIRE_TIME,TimeUnit.MINUTES);
		LOGGER.info("本次生成的验证码为[{}],已存放到Redis中,redis的key值为{}",verifyCode,RedisConstants.USER_CAPTCHA_CACHE_KEY+token);
		BufferedImage bufferedImage = VerifyCodeUtil.generateImageCode(verifyCode, 116, 36, 5, true, new Color(249,205,173), null, null);
		Map<String,String> map = Maps.newHashMap();
		map.put("key", token);
		map.put("image", ToolUtil.getBase64FromImage(bufferedImage));
		return Result.success(map);
	}

	
}