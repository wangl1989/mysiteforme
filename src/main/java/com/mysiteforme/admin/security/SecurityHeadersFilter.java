/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:16:25
 * @ Description: 安全响应头过滤器（CSP、X-Frame-Options、X-XSS-Protection、X-Content-Type-Options）
 */

package com.mysiteforme.admin.security;

import java.io.IOException;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 设置安全响应头
        // 更开放的 CSP 配置
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self' * data: blob: 'unsafe-inline' 'unsafe-eval'; " +  // 允许所有默认资源
                        "script-src 'self' * 'unsafe-inline' 'unsafe-eval'; " +  // 允许所有脚本源
                        "style-src 'self' * 'unsafe-inline'; " +  // 允许所有样式源
                        "img-src 'self' * data: blob: https: http:; " +  // 允许所有图片源
                        "font-src 'self' * data: https:; " +  // 允许所有字体源
                        "connect-src 'self' *; " +  // 允许所有API连接
                        "frame-src 'self' *; " +  // 允许所有iframe源
                        "media-src 'self' *");     // 允许所有媒体源

        // 如果是 /druid/* 路径，则不设置 X-Frame-Options
        String requestPath = ((HttpServletRequest) request).getRequestURI();
        if (!requestPath.startsWith("/druid/")) {
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        }


        //保留基本的 XSS 保护
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        chain.doFilter(request, response);
    }
}
