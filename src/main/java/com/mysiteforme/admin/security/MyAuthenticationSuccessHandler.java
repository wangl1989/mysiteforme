/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:30:04
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 13:04:04
 * @ Description: 登录成功处理
 */

package com.mysiteforme.admin.security;

import com.mysiteforme.admin.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 处理登录请求的Redis工具
     */
    private final SecurityService securityService;
 
    public MyAuthenticationSuccessHandler(SecurityService securityService) {
        this.securityService = securityService;
    }
 
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
        securityService.loginSuccess(user, request, response);
    }
}
