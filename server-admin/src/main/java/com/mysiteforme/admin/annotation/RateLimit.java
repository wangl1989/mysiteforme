package com.mysiteforme.admin.annotation;

import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.MessageConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * Redis Key前缀
     */
    String keyPrefix() default "rate_limit:";

    /**
     * 时间窗口内最大请求数
     */
    int limit() default 100;

    /**
     * 时间窗口长度(每1分钟的限制多少请求，这个1分钟就是period)
     */
    int period();

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流类型：IP, USER, API等
     */
    LimitType limitType() default LimitType.IP;

    /**
     * 超限提示信息
     */
    String message() default MessageConstants.Api.API_LIMIT_EXCEPTION;
}

