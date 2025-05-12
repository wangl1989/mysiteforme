/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:19:24
 * @ Description: XSS过滤器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;
import java.util.List;


import com.google.common.collect.Lists;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class XssFilter implements Filter {

    private static final List<String> EXCLUDE_URLS = Lists.newArrayList();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 检查是否需要跳过XSS过滤
        if (isExcludedUrl(httpRequest.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        // 使用修改后的 XssHttpServletRequestWrapper
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    private boolean isExcludedUrl(String servletPath) {
        return EXCLUDE_URLS.stream().anyMatch(servletPath::startsWith);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化Xss拦截器
        // throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void destroy() {
        // 拦截器销毁
//        throw new UnsupportedOperationException("Unimplemented method 'destroy'");
    }
    
    
}
