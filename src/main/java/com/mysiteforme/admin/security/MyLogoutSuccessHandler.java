/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 04:01:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 19:16:44
 * @ Description: 登出成功拦截器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.redis.LoginCache;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private final LoginCache loginCache;

    public MyLogoutSuccessHandler(LoginCache loginCache) {
        this.loginCache = loginCache;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        loginCache.logout(request, response);
    }
}
