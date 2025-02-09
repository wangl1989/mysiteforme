package com.mysiteforme.admin.base;

/*

  标识MyBatis的DAO,方便{@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。

  @author thinkgem

 * @version 2013-8-28

 */

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * MyBatis DAO标识注解
 * 用于标记MyBatis的DAO接口,方便扫描器识别
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface MyBatisDao {

    /**
     * 组件名称建议值
     * @return 建议的组件名称
     */
    String value() default "";

}

