/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 21:11:09
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 12:35:48
 * @ Description: 验证码拦截器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.SecurityService;
import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.Result;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
    
    private final SecurityService securityService;

    private final ApiToolUtil apiToolUtil;

    public CaptchaFilter (SecurityService securityService,ApiToolUtil apiToolUtil) {
        this.securityService = securityService;
        this.apiToolUtil= apiToolUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        
        String url = request.getRequestURI();
        RequestWrapper requestWrapper = new RequestWrapper(request);
        // 只拦截登录请求
        if ("/login".equals(url) && request.getMethod().equals("POST")) {
            try {
                securityService.validateCaptcha(requestWrapper, response);
            } catch (MyException e) {
                // 直接返回JSON响应
                apiToolUtil.returnSystemDate(Result.orgenalError(e.getCode(), e.getMsg()), request, response);
                return; // 停止过滤器链继续执行
            }
        }
        filterChain.doFilter(requestWrapper, response);

    }
    
}
