/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 21:11:09
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 12:35:48
 * @ Description: 验证码拦截器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import com.mysiteforme.admin.service.SecurityService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
    
    private final SecurityService securityService;

    public CaptchaFilter (SecurityService securityService) {
        this.securityService = securityService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        
        String url = request.getRequestURI();
        RequestWrapper requestWrapper = new RequestWrapper(request);
        // 只拦截登录请求
        if ("/login".equals(url) && request.getMethod().equals("POST")) {
            securityService.validateCaptcha(requestWrapper,response);
        }
        filterChain.doFilter(requestWrapper, response);
    }
    
}
