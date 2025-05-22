/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:43:47
 * @ Description: 常量类
 */

package com.mysiteforme.admin.util;

public class Constants {

	/**
	 *系统用户默认密码
	 */
	public static final String DEFAULT_PASSWORD = "123456";

	/**
	 * 定时任务参数名称
	 */
	public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

	/**
	 * 定时任务状态:正常
	 */
	public static final Integer QUARTZ_STATUS_NOMAL = 0;

	/**
	 * 设备ID常量
	 */
	public static final String DEVICE_ID = "Device-Id";

	/**
	 * 验证码前端传递进来body里的参数名称
	 */
	public static final String CAPTCHA = "captcha";

	/**
	 * 登录时输入的用户名
	 */
	public static final String USER_NAME = "username";

	/**
	 * webService定位API接口请求地址
	 */
	public static final String WEB_SERVICE_LOCATION_API = "https://apis.map.qq.com/ws/location/v1/ip?key=%s&ip=%s";

	/**
	 * 太平洋网络IP API
	 */
	public static final String PCON_LINE_API = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=%s";

	/**
	 * 默认用户名（注册的时候使用）
	 */
	public static final String DEFAULT_USER_NICK_NAME = "用户%s";

	/**
	 * header中认证字段
	 */
	public static final String AUTHORIZATION = "Authorization";

	/**
	 * 认证类型
	 */
	public static final String GRANT_TYPE = "Bearer ";

	/**
	 * 设置用户账号被锁定需要等待多长时间(分钟)后重新登录
	 */
	public static final int USER_WAIT_TO_LOGIN = 10;

	/**
	 * 设置用户设备被锁定需要等待多长时间(分钟)后重新登录
	 */
	public static final int USER_DEVICE_WAIT_TO_LOGIN = 24 * 60 ;

	/**
	 * 允许用户尝试登录次数
	 */
	public static final int ALLOW_USER_LOGIN_FAIL_COUNT = 5;

	/**
	 * 验证码有效期
	 */
	public static final int USER_CAPTCHA_CACHE_EXPIRE_TIME = 5;

	/**
	 * 验证邮箱有效期
	 */
	public static final int USER_EMAIL_CACHE_EXPIRE_TIME = 10;

	/**
	 * 验证成功1小时有效期，过期则不能注册或更换密码
	 */
	public static final int CHECK_EMAIL_SUCCESS_TIME = 1;

	/**
	 * 用户设备保存周期是30天
	 */
	public static final int USER_DEVICE_KEY_TIME = 30;
	/**
	 * 权限类型: 路由
	 */
	public static final int TYPE_PAGE = 1;

	/**
	 * 权限类型：按钮
	 */
	public static final int TYPE_BUTTON = 2;

	/**
	 * 权限类型：接口
	 */
	public static final int TYPE_API = 3;

	/**
	 * 本地上传的基础目录
	 */
	public static final String BASE_DIR = "upload/";


	public static final String DEFAULT_EMAIL_CHECK_REGISTE_SUBJECT = "验证账号注册";

	public static final String DEFAULT_EMAIL_REGIST_CONTENT = "您正在注册我们网站的会员，您的验证码是:%s，请在10分钟内输入验证码完成注册。";

	public static final String DEFAULT_EMAIL_REST_PASSWORD_SUBJECT = "重置账号密码";

	public static final String DEFAULT_EMAIL_REST_PASSWORD_CONTENT = "您正在重置您的账号密码，您的验证码是:%s，请在10分钟内输入验证码完成验证。";

}
