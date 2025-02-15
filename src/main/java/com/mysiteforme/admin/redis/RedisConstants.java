/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 17:57:59
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:26:38
 * @ Description: Redis常量类
 */

package com.mysiteforme.admin.redis;

public class RedisConstants {

    /**
	 * 用户登录缓存
	 */
	public static final String LOGIN_CACHE_KEY = "login_cache:";

	/**
	 * 用户登录失败次数缓存key
	 */
	public static final String USER_LOGIN_FAIL_CACHE_KEY = "user_login_fail:";

	/**
	 * 验证码缓存key
	 */
	public static final String USER_CAPTCHA_CACHE_KEY = "user_captcha:";


}
