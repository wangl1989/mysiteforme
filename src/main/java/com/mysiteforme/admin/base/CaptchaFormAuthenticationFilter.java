package com.mysiteforme.admin.base;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证码表单认证过滤器
 * 扩展Shiro的表单认证过滤器,增加验证码功能
 * @author wang
 * @since 2017/11/25
 */
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter {
    /**
     * 登录成功后的处理
     * 重写父类方法,使用重定向避免重复加载
     * @param token 认证令牌
     * @param subject 当前用户主体
     * @param request 请求对象
     * @param response 响应对象
     * @return 是否成功
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + this.getSuccessUrl());

        return true;
    }
}
