package com.mysiteforme.admin.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.stereotype.Component;

/**
 * 消息工具类
 * 
 * @author wangl
 * @date 2025/02/12
 */
@Component
public class MessageUtil implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static MessageSource messageSource;
    private static volatile boolean initialized = false;

    /**
     * 获取普通消息
     */
    public static String getMessage(String code) {
        // 如果未初始化完成，返回一个默认值
        if (!initialized) {
            return code;
        }
        String msg = messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        return StringUtils.isNotBlank(msg) ? msg : "参数:"+code+"未找到对应的配置";
    }
    
    /**
     * 获取带参数的消息
     * String welcome = MessageUtils.getMessage("user.welcome", "张三", "2025-02-13");
     * messages.properties中这样定义：user.welcome=欢迎{0}，您上次登录时间是{1}
     */
    public static String getMessage(String code, Object... args) {
        // 如果未初始化完成，返回一个默认值
        if (!initialized) {
            return code;
        }
        String msg = messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        return StringUtils.isNotBlank(msg) ? msg : "参数:"+code+"未找到对应的配置";
    }

    /**
     * 检查messageSource是否已初始化
     */
    private static void checkMessageSource() {
        if (messageSource == null) {
            // 不抛出异常，而是记录日志
            System.out.println("Warning: MessageSource not initialized yet");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageUtil.messageSource = applicationContext.getBean(MessageSource.class);
        initialized = true;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            System.err.println("Warning: MessageSource was not properly initialized!");
        }
    }
}
