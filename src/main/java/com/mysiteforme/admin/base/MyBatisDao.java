package com.mysiteforme.admin.base;

/*

  标识MyBatis的DAO,方便{@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。

  @author thinkgem

 * @version 2013-8-28

 */

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface MyBatisDao {

    /**

     * The value may indicate a suggestion for a logical component name,

     * to be turned into a Spring bean in case of an autodetected component.

     * @return the suggested component name, if any

     */
    String value() default "";

}

