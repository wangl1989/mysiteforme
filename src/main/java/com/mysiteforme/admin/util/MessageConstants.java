/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:00:52
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:37:14
 * @ Description: 消息常量类 用于存储消息的常量
 */

package com.mysiteforme.admin.util;

public class MessageConstants {
    public static final String SUCCESS = "message.success";
    public static final String FAILED = "message.failed";
    
    public static class User {
        public static final String USERNAME_EMPTY = "message.user.username.empty";
        public static final String PASSWORD_ERROR = "message.user.password.error";
        public static final String USER_NO_PERMISSION = "message.user.no.permission";
        public static final String USER_NO_LOGIN = "message.user.no.login";
        // 登出成功
        public static final String USER_LOGOUT_SUCCESS = "message.user.logout.success";
        // 登录失败 
        public static final String USER_LOGIN_FAILED = "message.user.login.failed";
        // 登录失败次数过多
        public static final String USER_LOGIN_FAILED_LIMIT = "message.user.login.failed.limit";
        // 未授权或登录已过期
        public static final String USER_LOGIN_FAILED_UNAUTHORIZED = "message.user.login.failed.unauthorized";
        // 无权限访问
        public static final String USER_LOGIN_FAILED_FORBIDDEN = "message.user.login.failed.forbidden";
        // 验证码不能为空
        public static final String USER_CAPTCHA_NULL = "message.user.captcha.null";
        // 验证码已过期
        public static final String USER_CAPTCHA_EXPIRED = "message.user.captcha.expired";
        // 验证码错误
        public static final String USER_CAPTCHA_ERROR = "message.user.captcha.error";
    }

    public static class Exception {
        public static final String EXCEPTION_LOGIN = "message.exception.login";
        public static final String EXCEPTION_LOGIN_LIMIT = "message.exception.login.limit";
        public static final String EXCEPTION_LOGIN_OUT = "message.exception.login.out";
        public static final String EXCEPTION_REDIS_DATA = "message.exception.redis.data";
        public static final String EXCEPTION_USER_NOT_FOUND = "message.exception.user.not.found";
        public static final String EXCEPTION_USER_NO_ROLE = "message.exception.user.no.role";
        public static final String EXCEPTION_USER_NO_PERMISSION = "message.exception.user.no.permission";
    }

    public static class Redis {
        public static final String REDIS_DATA_EXCEPTION = "message.redis.data.exception";
    }

    public static class Api {
        // 请求太快了，请稍后再试
        public static final String API_LIMIT_EXCEPTION = "message.api.limit";
    }

    public static class System {
        public static final String SYSTEM_ERROR = "message.system.error";
    }

    public static class Validate {
        public static final String VALIDATE_ID_ERROR = "message.validate.id.error";
    }

}
