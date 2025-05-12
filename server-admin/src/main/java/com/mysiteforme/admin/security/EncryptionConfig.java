package com.mysiteforme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncryptionConfig {

    // 将 MyPasswordEncoder 的注入移到这里，如果它也是一个 Bean
    private final MyPasswordEncoder myPasswordEncoder;

    public EncryptionConfig(MyPasswordEncoder myPasswordEncoder) {
        this.myPasswordEncoder = myPasswordEncoder;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 如果 MyPasswordEncoder 本身就是 PasswordEncoder 的实现，可以直接返回
        // return myPasswordEncoder;
        // 或者如果 MyPasswordEncoder 需要进一步配置或包装，在这里处理
        // 假设 MyPasswordEncoder 已经是你要用的实例
        return this.myPasswordEncoder;
    }
}
