/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:26:18
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 15:41:05
 * @ Description: security密码管理器
 */

package com.mysiteforme.admin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密：BCryptPasswordEncoder
 * @author wangl
 * @date 2025/02/13
 */
@Slf4j
@Component
public class MyPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MyPasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bCryptPasswordEncoder.encode((String) rawPassword);
    }
 
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.info("==========进入密码校验方法=============");
        log.info("====进入密码校验方法====");
        log.info("原始密码: {}", rawPassword);
        log.info("加密密码: {}", encodedPassword);
        return bCryptPasswordEncoder.matches(rawPassword,encodedPassword);
    }
}
