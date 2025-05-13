package com.mysiteforme.admin.aspect;

import com.mysiteforme.admin.util.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String keyPrefix() default "rate_limit:"; // Redis Key前缀

    int limit() default 100; // 时间窗口内最大请求数

    int period(); // 时间窗口长度

    TimeUnit timeUnit() default TimeUnit.SECONDS; // 时间单位

    LimitType limitType() default LimitType.IP; // 限流类型：IP, USER, API等

    String message() default "请求过于频繁，请稍后再试"; // 超限提示信息
}

