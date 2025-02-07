package com.mysiteforme.admin.base;

import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.service.SiteService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 博客系统拦截器
 * 用于处理博客相关的请求拦截
 * @author wangl
 * @since 2017/11/30
 */
@Component
public class BlogHandlerInterceptor implements HandlerInterceptor {

    private SiteService siteService;

    /**
     * 无参构造函数
     */
    public BlogHandlerInterceptor() {
        super();
    }

    /**
     * 带SiteService参数的构造函数
     * @param siteService 站点服务接口
     */
    @Autowired
    public BlogHandlerInterceptor(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * 请求预处理
     * 在请求处理之前进行调用,获取当前站点信息并设置到request中
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true:继续流程 false:中断流程
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (siteService == null) {//解决service为null无法注入问题
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            siteService = (SiteService) factory.getBean("siteService");

        }
        Site site = siteService.getCurrentSite();
        if(site != null){
            request.setAttribute("site",site);
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
