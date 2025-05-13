/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:17:09
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:31:41
 * @ Description: 配置消息源 用于国际化消息的加载
 */

package com.mysiteforme.admin.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;


@Configuration
public class MessageSourceConfig {
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // 指定消息文件路径 可以是外部的
        messageSource.setBasename("classpath:properties/messages");
        // 指定编码
        messageSource.setDefaultEncoding("UTF-8");
        // 设置缓存时间（单位：秒）
        // 设置为-1表示每次都重新加载
        // 开发环境：可以设置较短时间，方便测试
        // 生产环境：可以设置较长时间，提高性能
        messageSource.setCacheSeconds(1000);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINA);
        return resolver;
    }
}

