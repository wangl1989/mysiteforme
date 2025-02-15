/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 04:01:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 19:16:44
 * @ Description: 登出成功拦截器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import com.mysiteforme.admin.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SecurityService securityService;

    public MyLogoutSuccessHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        securityService.logout(request, response);
    }
}
