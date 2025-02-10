package com.mysiteforme.admin.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wangl on 2018/1/24.
 * todo:
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class MyException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    // 验证错误
    public static final int VALIDATION_ERROR = 401;

    // 业务错误
    public static final int BUSINESS_ERROR = 400;

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

}
