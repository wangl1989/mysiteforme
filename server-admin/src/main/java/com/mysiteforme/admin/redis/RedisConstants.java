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

	/**
	 * 用户设备活动缓存
	 */
	public static final String DEVICE_ACTIVITY_USER_PREFIX = "device:activity:%s:";
	/**
	 * 未登录用户设备活动缓存
	 */
	public static final String DEVICE_ACTIVITY_ANONYMOUS_PREFIX = "device:activity:anonymous:";

	/**
	 * 今天之前的用户ID集合KEY
	 */
	public static final String USER_BEFOR_TODAY_CREATED = "analytics:user:created:";

	/**
	 * 访问计数
	 */
	public static final String ANALYTICS_VISITS_KEY = "analytics:stats:visits:";

	/**
	 * 月度访问计数
	 */
	public static final String ANALYTICS_MONTH_VISITS_KEY = "analytics:stats:month:visits:";

	/**
	 * 独立访客
	 */
	public static final String ANALYTICS_VISITORS_KEY = "analytics:stats:visitors:";

	/**
	 * 新用户
	 */
	public static final String ANALYTICS_NEW_USERS_KEY = "analytics:stats:newUsers:";

	/**
	 * 点击计数
	 */
	public static final String ANALYTICS_CLICKS_KEY = "analytics:stats:clicks:";

	/**
	 * 访问时长
	 */
	public static final String ANALYTICS_DURATION_KEY = "analytics:stats:duration:";

	/**
	 * 访问时长次数key
	 */
	public static final String ANALYTICS_DURATION_COUNT_KEY = "analytics:stats:durationCount:";
	/**
	 * 当前总访问量
	 */
	public static final String ANALYTICS_TOTAL_VISITS_KEY = "analytics:total:visits";

	/**
	 * 当前总点击量
	 */
	public static final String ANALYTICS_TOTAL_CLICK_KEY = "analytics:total:click";

	/**
	 * 当前总用户数量
	 */
	public static final String ANALYTICS_TOTAL_USER_KEY = "analytics:total:user:";

	/**
	 * 获取截至当天各项总数
	 */
	public static final String ANALYTICS_TOTAL_DATA_KEY = "analytics:total:data:";
	/**
	 * 截至上周总访问量
	 */
	public static final String ANALYTICS_LAST_WEEK_TOTAL_KEY = "analytics:lastWeek:totalData:";
	/**
	 * 上周每天平均数据KEY
	 */
	public static final String ANALYTICS_LAST_WEEK_DATA_KEY = "analytics:lastWeek:avgData:";

	/**
	 * 每天的访问数据趋势key
	 */
	public static final String ANALYTICS_DAY_TREND_DATA_KEY = "analytics:trend:day:";
	/**
	 * 每年的访问数据趋势key
	 */
	public static final String ANALYTICS_MONTH_TREND_DATA_KEY = "analytics:trend:month";

	/**
	 * 用户首页数据缓存
	 */
	public static final String ANALYTICS_USER_LIST_KEY = "analytics:index:user";

	/**
	 * 首页用户操作日志缓存key
	 */
	public static final String ANALYTICS_USER_OPERATOR_LOG_KEY = "analytics:index:userLog";

	/**
	 * 首页默认操作日志数量
	 */
	public static final Integer ANALYTICS_INDEX_LOG_SIZE = 6;

	/**
	 * 用户系统数据key
	 */
	public static final String ANALYTICS_USER_SYSTEM_DATA_KEY = "analytics:user:systemData";

	/**
	 * 获取系统API请求地址和图标映射关系KEY
	 */
	public static final String ANALYTICS_API_URL_TO_ICON_KEY = "analytics:api:urlToIcon";

	/**
	 * 当前登录TOKEN的KEY
	 */
	public static final String ACCESS_TOKEN_STR_FORMAT_KEY = "auth:access:%s:%s";

	/**
	 * 当前登录刷新TOKEN的KEY
	 */
	public static final String REFRESH_TOKEN_STR_FORMAT_KEY = "auth:refresh:%s:%s";

	/**
	 * ai聊天历史记录
	 */
	public static final String AI_HISTORY_KEY_PREFIX = "ai:chat:history:";

	/**
	 * 设备关联聊天历史记录
	 */
	public static final String AI_DEVICE_HISTORY_KEY_PREFIX = "ai:chat:deviceId:";

	/**
	 * AI 代码设计初始需求
	 */
	public static final String AI_REQUIREMENT_KET_PREFIX = "ai:chat:requirement:";

	/**
	 * AI 获取需求对应的实体类
	 */
	public static final String AI_REQUIREMENT_ENTITY_KET_PREFIX = "ai:chat:requirement:entity:";

	/**
	 * AI 获取实体类之间的关系
	 */
	public static final String AI_REQUIREMENT_RELATIONSHIP_KET_PREFIX = "ai:chat:requirement:relationship:";

	/**
	 * AI 获取额外的需求KEY
	 */
	public static final String AI_REQUIREMENT_ADDITIONAL_KET_PREFIX = "ai:chat:requirement:additional:";

}
