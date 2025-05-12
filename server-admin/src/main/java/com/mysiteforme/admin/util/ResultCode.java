/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:42:09
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:39:57
 * @ Description: 结果码类 用于定义结果码
 */

package com.mysiteforme.admin.util;

public class ResultCode {
  
    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 验证邮箱成功
     */
    public static final int CHECK_EMAIL_SUCCESS = 203;

    /**
     * 客户端错误 (4xx)
     */
    
    /**
     * 请求参数错误
     */
    public static final int BAD_PARAM = 400;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;
    /**
     * 请求方法不允许
     */
    public static final int METHOD_NOT_ALLOWED = 405;
    /**
     * 资源冲突
     */
    public static final int CONFLICT = 409;

    /**
     * 登录失败
     */
    public static final int LOGIN_ERROR = 410;
    /**
     * 登录失败次数过多
     */
    public static final int LOGIN_FAILED_LIMIT = 411;
    
    /**
     * 服务端错误 (5xx)
     */

     /**
      * 系统内部错误
      */
    public static final int INTERNAL_ERROR = 500;
    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE = 503;
    
    /**
     * 自定义业务错误码(6xx)
     */
    /**
     * 参数校验失败
     */
    public static final int INVALID_PARAM = 600; 
    /**
     * 业务处理失败
     */
    public static final int BUSINESS_ERROR = 601;
    /**
     * 令牌过期
     */
    public static final int TOKEN_EXPIRED = 602;

    /**
     * 结果校验不存在
     */
    public static final int INVALID_RESULT = 604; 
    /**
     * token错误
     */
    public static final int INVALID_TOKEN = 610;

    /**
     * 刷新token错误
     */
    public static final int INVALID_REFRESH_TOKEN = 611;
}
