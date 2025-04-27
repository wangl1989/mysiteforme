package com.mysiteforme.admin.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * 用于标记需要记录系统操作日志的方法
 * @author wangl
 * @since 2018/1/13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 日志描述信息
     * @return 描述信息字符串,用于记录操作的具体内容
     */
    String value() default "";
}
