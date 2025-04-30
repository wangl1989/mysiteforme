/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 17:57:59
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 17:01:26
 * @ Description: Redis常量类
 */

package com.mysiteforme.admin.redis;

public class RedisConstants {


	/**
	 * 用户登录失败次数缓存key
	 */
	public static final String USER_LOGIN_FAIL_CACHE_KEY = "user:login_fail:";

	/**
	 * 验证码缓存key
	 */
	public static final String USER_CAPTCHA_CACHE_KEY = "user:captcha:";

	/**
	 * 用户邮箱key
	 */
	public static final String USER_EMAIL_KEY = "user:email:";

	/**
	 * 重置用户密码key
	 */
	public static final String USER_EMAI_REST_PASSWORD_KEY = "user:email:rest:password:";

	/**
	 * 用户验证成功的邮箱key
	 */
	public static final String EMAIL_VALIDATE_SUCCESS = "user:email:validate:success";
	/**
	 * 重置密码成功的邮箱key
	 */
	public static final String EMAIL_REST_PASSWORD_VALIDATE_SUCCESS = "user:email:resetPassword:validate:success";


	/**
	 * 刷新后的新的缓存前缀
	 */
	public static final String ACCESS_TOKEN_PREFIX = "auth:access:";

	/**
	 * 用户刷新用户的token
	 */
	public static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";

	/**
	 * 用户设备映射前缀---日常访问令牌
	 */
	public static final String USER_DEVICE_PREFIX = "auth:user:device:";

}
