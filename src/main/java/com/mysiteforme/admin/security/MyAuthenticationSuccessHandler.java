/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:30:04
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:03:32
 * @ Description: 登录成功处理
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import com.mysiteforme.admin.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication
    ) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }
 
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
        securityService.loginSuccess(user, response);
    }
}
