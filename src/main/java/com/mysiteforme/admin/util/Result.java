/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:43:17
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 22:16:51
 * @ Description: 返回结果对象
 */

package com.mysiteforme.admin.util;

import java.util.HashMap;

public class Result extends HashMap<String,Object> {
    
    /**
     * 通用返回成功对象
     * @return 返回结果对象
     */
    public static Result success(){
        return success(MessageUtil.getMessage(MessageConstants.SUCCESS));
    }

    /**
     * 带字段定义消息的返回对象
     * @param message 消息
     * @return 返回结果对象
     */
    public static Result success(String message){
        return success(message,null);
    }

    /**
     * 带数据返回对象(没有自定义消息)
     * @param data 数据
     * @return 返回结果对象
     */
    public static Result success(Object data){
        return success(MessageUtil.getMessage(MessageConstants.SUCCESS),data);
    }

    /**
     * 带字段定义消息以及数据的返回对象
     * @param message 消息
     * @param data 数据
     * @return 返回结果对象
     */
    public static Result success(String message,Object data){
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        result.setCode(ResultCode.SUCCESS);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 返回原始错误对象
     * @param code 状态码
     * @param message 消息
     * @return 返回结果对象
     */
    public static Result orgenalError(Integer code,String message){
        return error(code,message,null);
    }

    /**
     * 通用返回错误对象
     * @param code 状态码
     * @param message 消息
     * @return 返回结果对象
     */
    public static Result error(Integer code,String message){
        return error(code,MessageUtil.getMessage(message),null);
    }

    /**
     * 带状态码，字段定义消息，数据的返回对象
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     * @return 返回结果对象
     */
    public static Result error(Integer code,String message,Object data){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message); 
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public Result setSuccess(Boolean status){
        if (status == null){
            status = false;
        } 
        put("success", status);
        return this;
    }

    public boolean isSuccess() {
        Object value = get("success");
        return value != null && (boolean) value;
    }

    public Integer getCode() {
        Object value = get("code");
        return value != null?(Integer) value:ResultCode.INTERNAL_ERROR;
    }

    /**
     * 状态码
     */
    public Result setCode(Integer code) {
        if (code == null){
            code = ResultCode.SERVICE_UNAVAILABLE;
        } 
        put("code", code);
        return this;
    }

    /**
     * 提示信息
     */
    public Result setMessage(String message) {
        put("message", message);
        return this;
    }

    /**
     * 数据
     */
    public Result setData(Object data) {
        put("data", data);
        return this;
    }

    /**
     * 时间戳
     */
    public Result setTimestamp(long timestamp) {
        if (timestamp != 0) put("timestamp", timestamp);
        return this;
    }


    /**
     * 系统错误
     */
    public static Result systemError() {
        return error(ResultCode.INTERNAL_ERROR, MessageConstants.System.SYSTEM_ERROR);
    }

    /**
     * 返回参数错误对象
     * @param message MessageConstants中的常量
     * @return 错误对象
     */
    public static Result paramMsgError(String message) {
        return error(ResultCode.INVALID_PARAM,message);
    }

    /**
     * 返回token验证失效
     * @param message MessageConstants中的常量
     * @return 错误对象
     */
    public static Result tokenInvalidError(String message) {
        return error(ResultCode.INVALID_TOKEN,message);
    }

    /**
     * 返回刷新token验证失效
     * @param message MessageConstants中的常量
     * @return 错误对象
     */
    public static Result refreshTokenInvalidError(String message) {
        return error(ResultCode.INVALID_REFRESH_TOKEN,message);
    }

    /**
     * ID不能为空错误
     */
    public static Result idIsNullError(){
        return error(ResultCode.INVALID_PARAM, MessageConstants.Validate.VALIDATE_ID_ERROR);
    }

    /**
     * 业务错误
     * @param message MessageConstants中的常量
     * @return 错误对象
     */
    public static Result businessMsgError(String message) {
        return error(ResultCode.BUSINESS_ERROR,message);
    }

    public static Result objectNotNull(){
        return error(ResultCode.BUSINESS_ERROR, MessageConstants.OBJECT_NOT_NULL);
    }

    /**
     * 未授权
     */
    public static Result unauthorized() {
        return error(ResultCode.UNAUTHORIZED, MessageConstants.User.USER_LOGIN_FAILED_UNAUTHORIZED);
    }

    /**
     * 禁止访问
     */
    public static Result forbidden() {
        return error(ResultCode.FORBIDDEN, MessageConstants.User.USER_LOGIN_FAILED_FORBIDDEN);
    }

}

