package com.mysiteforme.admin.config;

import com.google.common.collect.Lists;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

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
}
