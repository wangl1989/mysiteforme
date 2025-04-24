/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 03:09:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 13:01:04
 * @ Description: 登录失败处理器
 */

package com.mysiteforme.admin.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.service.SecurityService;
import com.mysiteforme.admin.util.ApiToolUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final SecurityService securityService;

    private final ApiToolUtil apiToolUtil;

    public MyAuthenticationFailureHandler(SecurityService securityService, ApiToolUtil apiToolUtil) {
        this.securityService = securityService;
        this.apiToolUtil = apiToolUtil;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        log.error("登录失败：{}", exception.getMessage(), exception);
        apiToolUtil.returnSystemDate(securityService.loginFailData(request,response),request,response);
    }
}
