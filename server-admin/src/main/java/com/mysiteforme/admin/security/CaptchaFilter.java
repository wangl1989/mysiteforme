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
        boolean isLoginPost = "/login".equals(url) && "POST".equalsIgnoreCase(request.getMethod());
        boolean isJson = request.getContentType() != null && request.getContentType().toLowerCase().contains("application/json");
        // 只拦截登录请求
        if (isLoginPost && isJson) {
            // 使用 ContentCachingRequestWrapper
            RequestWrapper requestWrapper = new RequestWrapper(request);
            // 读取 Body (即使在 doFilter 之前，ContentCachingRequestWrapper 第一次读取后会缓存)
            // 或者，如果必须在 doFilter 之前读取，就需要手动调用 getInputStream().read() 或类似方法触发读取和缓存
            // 但更简单的方式是下面这样，结合后续的 getCaptchaFromCachedBody
            try {
                // 注意：第一次读取会消耗原始流并缓存，所以需要在验证前完成
                // 这里可以先尝试读取一次来触发缓存，虽然有点技巧性
                // requestWrapper.getInputStream().readAllBytes(); // 强制读取并缓存

                // 更稳妥的做法是直接传递 requestWrapper，在需要时再读取 Body
                // 假设 securityService.validateCaptcha 需要 HttpServletRequest
                securityService.validateCaptcha(requestWrapper, response);
                // 如果验证通过，继续 Filter 链，Spring Security 可以从 requestWrapper 读取 body
                filterChain.doFilter(requestWrapper, response);
            } catch (MyException e) {
                // 直接返回JSON响应
                apiToolUtil.returnSystemDate(Result.orgenalError(e.getCode(), e.getMsg()), request, response);
            }
        }else{
            filterChain.doFilter(request, response);
        }


    }
    
}
