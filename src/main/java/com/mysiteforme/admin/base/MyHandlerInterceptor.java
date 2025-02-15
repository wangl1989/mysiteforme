/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:29:56
 * @ Description: 系统通用拦截器，用于处理系统通用的请求拦截
 */

package com.mysiteforme.admin.base;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class MyHandlerInterceptor implements HandlerInterceptor {

    private  SiteService siteService;
    private  UserService userService;
    private  UserCacheService userCacheService;

    public MyHandlerInterceptor() {
        super();
    }

    @Autowired
    public MyHandlerInterceptor(SiteService siteService, UserService userService, UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
        this.siteService = siteService;
        this.userService = userService;
    }

    /**
     * 前置处理,在请求处理之前进行调用
     * @return true:继续流程 false:中断流程
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Object o) {
//        LOGGER.info("当前请求路径.."+httpServletRequest.getRequestURI());
        if (siteService == null || userService == null|| userCacheService == null) {
            //解决service为null无法注入问题
            System.out.println("siteService is null!!!");
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(httpServletRequest.getServletContext());
            siteService = (SiteService) factory.getBean("siteService");
            userService = (UserService) factory.getBean("userService");
            userCacheService = (UserCacheService) factory.getBean("userCacheService");

        }
        Site sysSite = siteService.getCurrentSite();
        httpServletRequest.setAttribute("site",sysSite);
        MyUserDetails myUserDetails = MySecurityUser.securityUser();
        if(myUserDetails == null){
            return false;
        }
        User user = userCacheService.findUserById(myUserDetails.getId());
        if(user != null){
            httpServletRequest.setAttribute("currentUser",user);
            return true;
        }
        return false;
    }

    /**
     * 请求处理之后进行调用,但是在视图被渲染之前
     */
    @Override
    public void postHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Object o, ModelAndView modelAndView) {

    }

    /**
     * 在整个请求结束之后被调用
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Object o, Exception e) {

    }
}
