/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:43:47
 * @ Description: 常量类
 */

package com.mysiteforme.admin.util;

/**
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 23:03:56
 * 
 * @author wangl
 */
public class Constants {
	/**
	 * shiro采用加密算法
	 */
	public static final String HASH_ALGORITHM = "SHA-1";
	/**
	 * 生成Hash值的迭代次数 
	 */
	public static final int HASH_INTERATIONS = 1024;
	/**
	 * 生成盐的长度
	 */
	public static final int SALT_SIZE = 8;

	/**
	 * 验证码
	 */
	public static final String VALIDATE_CODE = "validateCode";

	/**
	 *系统用户默认密码
	 */
	public static final String DEFAULT_PASSWORD = "123456";

	/**
	 * 定时任务状态:正常
	 */
	public static final Integer QUARTZ_STATUS_NOMAL = 0;
	/**
	 * 定时任务状态:暂停
	 */
	public static final Integer QUARTZ_STATUS_PUSH = 1;

	/**
	 * 评论类型：1文章评论
	 */
	public static final Integer COMMENT_TYPE_ARTICLE_COMMENT = 1;
	/**
	 * 评论类型：2.系统留言
	 */
	public static final Integer COMMENT_TYPE_LEVING_A_MESSAGE = 2;

	/**
	 * 验证码的token
	 */
	public static final String CAPTCHA_TOKEN = "Captcha-Key";

	/**
	 * 设备ID常量
	 */
	public static final String DEVICE_ID = "Device-Id";

	/**
	 * 验证码前端传递进来body里的参数名称
	 */
	public static final String CAPTCHA = "captcha";



	/**
	 * header中认证字段
	 */
	public static final String AUTHORIZATION = "Authorization";

	/**
	 * 认证类型
	 */
	public static final String GRANT_TYPE = "Bearer ";

	/**
	 * 设置用户需要等待多长时间(分钟)后重新登录
	 */
	public static final int USER_WAIT_TO_LOGIN = 10;

	/**
	 * 允许用户尝试登录次数
	 */
	public static final int ALLOW_USER_LOGIN_FAIL_COUNT = 5;

	/**
	 * 接口限流设置：允许API接口访问的间隔时间  单位：秒
	 */
	public static final int ALLOW_USER_ACTION_SECOND = 1;

	/**
	 * 用户登录token过期时间 单位：分钟
	 */
	public static final int USER_LOGIN_TOKEN_EXPIRE_TIME = 60;

	/**
	 * 验证码有效期
	 */
	public static final int USER_CAPTCHA_CACHE_EXPIRE_TIME = 5;

	public static final int TYPE_PAGE = 1;

	public static final int TYPE_BUTTON = 2;

	public static final int TYPE_API = 3;

}
