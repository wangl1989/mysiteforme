/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 03:09:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 17:47:17
 * @ Description: 登录失败处理器
 */

package com.mysiteforme.admin.security;

import java.io.IOException;

import com.mysiteforme.admin.service.SecurityService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final SecurityService securityService;

    public MyAuthenticationFailureHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        // 获取用户名
        String username = request.getParameter("username");
        if (StringUtils.isEmpty(username)) {
            log.error("用户登录异常：用户参数名称错误，未获取到名为name的参数");
            throw MyException.builder().systemError(MessageUtil.getMessage(MessageConstants.User.USER_LOGIN_FAILED))
                    .build();
        }

        ApiToolUtil.returnSystemDate(securityService.loginFailData(username), response);
    }
}
