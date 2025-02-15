/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:30:04
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 18:43:28
 * @ Description:
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.redis.LoginCache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 处理登录请求的Redis工具
     */
    private final LoginCache loginCache;
 
    public MyAuthenticationSuccessHandler(LoginCache loginCache) {
        this.loginCache = loginCache;
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
        loginCache.loginSuccess(user, response);
    }
}
