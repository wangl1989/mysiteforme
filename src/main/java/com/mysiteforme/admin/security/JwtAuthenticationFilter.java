/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:02:36
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 21:10:23
 * @ Description:JWT过滤器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mysiteforme.admin.redis.LoginCache;
import com.mysiteforme.admin.util.Constants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final LoginCache loginCache;

    public JwtAuthenticationFilter(LoginCache loginCache) {
        this.loginCache = loginCache;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        //  从header中获取验证信息
        String authHeader = request.getHeader(Constants.AUTHORIZATION);
        if (ObjectUtils.isEmpty(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        this.doParse(request, response, filterChain, authHeader);
    }
 
    private void doParse(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, String authHeader
    ) throws ServletException, IOException {
        //  如果认证码  以规定值开头
        if (authHeader.startsWith(Constants.GRANT_TYPE)) {

            if(!loginCache.checkToken(authHeader,request)){
                chain.doFilter(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}