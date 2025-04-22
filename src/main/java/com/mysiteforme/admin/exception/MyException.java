/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 18:58:10
 * @ Description: 自定义异常类
 */

package com.mysiteforme.admin.exception;

import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
import com.mysiteforme.admin.util.ResultCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // 验证错误
    public static final int VALIDATION_ERROR = 401;

    // 业务错误
    public static final int BUSINESS_ERROR = 400;

    // 登录错误
    public static final int LOGIN_ERROR = 405;

    // 系统错误
    public static final int SERVER_ERROR = 500;

    // 错误视图地址
    public static final String ERROR_PAGE = "admin/error/500";

    // 未发现视图地址
    public static final String NOT_FOUND_PAGE = "admin/error/404";

   /**
     * 错误信息
     */
    private String msg;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误类型
     */
    private String errorType;

    /**
     * 错误页面
     */
    private String viewName;

    /**
     * 具体异常
     */
    private Throwable throwable;

    private MyException(Builder builder) {
        super(builder.msg);
        this.msg = builder.msg;
        this.code = builder.code;
        this.errorType = builder.errorType;
        this.viewName = builder.viewName;
        this.throwable = builder.throwable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String msg;
        private int code;
        private String errorType;
        private String viewName;
        private Throwable throwable;

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder errorType(String errorType) {
            this.errorType = errorType;
            return this;
        }

        public Builder viewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }
        
        public Builder systemError(String msg){
            this.msg = MessageUtil.getMessage(msg);
            this.code = ResultCode.INTERNAL_ERROR;
            return this;
        }

        public Builder validationError(String msg){
            this.msg = msg;
            this.code = ResultCode.INVALID_RESULT;
            return this;
        }

        public Builder tokenError(String msg){
            this.msg = MessageUtil.getMessage(msg);
            this.code = ResultCode.INVALID_TOKEN;
            return this;
        }

        public Builder tokenExpired(){
            this.msg = MessageUtil.getMessage(MessageConstants.JwtToken.JWT_TOKEN_EXPIRED);
            this.code = ResultCode.TOKEN_EXPIRED;
            return this;
        }

        public Builder paramMsgError(String msg){
            this.msg = MessageUtil.getMessage(msg);
            this.code = ResultCode.INVALID_PARAM;
            return this;
        }

        public Builder businessError(String msg){
            this.msg = MessageUtil.getMessage(msg);
            this.code = ResultCode.BUSINESS_ERROR;
            return this;
        }

        public Builder businessError(String msg, Object... args){
            this.msg = MessageUtil.getMessage(msg,args);
            this.code = ResultCode.BUSINESS_ERROR;
            return this;
        }

        /**
         * 未授权
         */
        public Builder unauthorized() {
            this.msg = MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED_UNAUTHORIZED);
            this.code = ResultCode.UNAUTHORIZED;
            return this;
        }

        /**
         * 禁止访问
         */
        public Builder forbidden() {
            this.msg = MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED_FORBIDDEN);
            this.code = ResultCode.FORBIDDEN;
            return this;
        }

        public MyException build() {
            return new MyException(this);
        }
    }
}
