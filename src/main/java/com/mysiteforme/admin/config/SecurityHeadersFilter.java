package com.mysiteforme.admin.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 设置安全响应头
        // 更开放的 CSP 配置
//        httpResponse.setHeader("Content-Security-Policy",
//                "default-src 'self' * data: blob: 'unsafe-inline' 'unsafe-eval'; " +  // 允许所有默认资源
//                        "script-src 'self' * 'unsafe-inline' 'unsafe-eval'; " +  // 允许所有脚本源
//                        "style-src 'self' * 'unsafe-inline'; " +  // 允许所有样式源
//                        "img-src 'self' * data: blob: https: http:; " +  // 允许所有图片源
//                        "font-src 'self' * data: https:; " +  // 允许所有字体源
//                        "connect-src 'self' *; " +  // 允许所有API连接
//                        "frame-src 'self' *; " +  // 允许所有iframe源
//                        "media-src 'self' *");     // 允许所有媒体源
//
//        // 移除 X-Frame-Options 限制
//        // httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");  // 注释掉或删除这行
//
//        // 保留基本的 XSS 保护
//        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
//        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        chain.doFilter(request, response);
    }
}
