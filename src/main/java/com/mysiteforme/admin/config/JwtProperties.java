package com.mysiteforme.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret;

    /**
     * token过期时间
     */
    private long accessTokenExpiration;

    /**
     * 刷新令牌过期时间
     */
    private long refreshTokenExpiration;
}



