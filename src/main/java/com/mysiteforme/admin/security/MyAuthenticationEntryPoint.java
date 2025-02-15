/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 23:48:50
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 17:55:23
 * @ Description: 拦截未登录请求
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 拦截未登录请求：
     * 用户发起未登录的请求会被AuthorizationFilter拦截，并抛出AccessDeniedException异常。异常被AuthenticationEntryPoint
     * 处理，默认会触发重定向到登录页。Spring Security开放了配置，允许我们自定义AuthenticationEntryPoint。
     * 那么我们就通过自定义AuthenticationEntryPoint来取消重定向行为，将接口改为返回JSON信息。
     * @param request 请求
     * @param response 响应
     * @param authException 认证异常
     * @throws IOException 输入输出异常
     * @throws ServletException 服务异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiToolUtil.returnSystemDate(Result.error(ResultCode.LOGIN_ERROR, MessageUtil.getMessage(MessageConstants.User.USER_NO_LOGIN)), response);
    }
}
