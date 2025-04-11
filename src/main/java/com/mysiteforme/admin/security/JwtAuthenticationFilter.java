/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:02:36
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 21:02:41
 * @ Description:JWT过滤器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import com.mysiteforme.admin.service.SecurityService;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mysiteforme.admin.util.Constants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final SecurityService securityService;

    public JwtAuthenticationFilter(SecurityService securityService) {
        this.securityService = securityService;
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
        this.doParse(request, response, filterChain);
    }
 
    private void doParse(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if(!securityService.checkToken(request, response)){
            return;
        }
        chain.doFilter(request, response);
    }
}