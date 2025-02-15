/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 00:19:07
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:03:22
 * @ Description: 权限认证失败返回结果
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        ApiToolUtil.returnSystemDate(Result.unauthorized(), response);
    }
}
