/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 21:09:57
 * @ Description: WebMvc配置类 配置视图解析、资源映射、消息转换等
 */

package com.mysiteforme.admin.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mysiteforme.admin.util.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.base.BlogHandlerInterceptor;
import com.mysiteforme.admin.base.MyHandlerInterceptor;
import com.mysiteforme.admin.security.CustomExceptionHandlerFilter;
import com.mysiteforme.admin.security.SecurityHeadersFilter;
import com.mysiteforme.admin.security.XssFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomExceptionHandlerFilter filter;

    private final ApiToolUtil apiToolUtil;

    @Value("${user-path.windows-upload-dic}")
    private String windowsBaseUploadDir;

    @Value("${user-path.linux-upload-dic}")
    private String linuxBaseUploadDir;

    public WebMvcConfig(CustomExceptionHandlerFilter filter, ApiToolUtil apiToolUtil) {
        this.filter = filter;
        this.apiToolUtil = apiToolUtil;
    }

    /**
     * 地址找不到（存疑，是否和全局异常里的地址找不到判断功能重复？）
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add((request, response, handler, ex) -> {
            if (ex instanceof NoResourceFoundException) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                // 可以写入自定义的响应内容
                apiToolUtil.returnSystemDate(Result.error(ResultCode.NOT_FOUND, MessageConstants.System.SYSTEM_NOT_FOUND),request,response);
            }
            return null;
        });
    }

    /**
     * 配合XSS过滤器过滤器生成一个全局bean
     */
    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }

    /**
     * 具体配置Security请求的安全头
     */
    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    /**
     * 配置全局自定义异常处理过滤器，处在Security流程链路最前端
     * 范围：除静态资源目录以及druid目录等
     */
    @Bean
    public FilterRegistrationBean<CustomExceptionHandlerFilter> exceptionFilterRegistration() {
        FilterRegistrationBean<CustomExceptionHandlerFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        registration.addUrlPatterns("/*"); // 应用到所有URL
        return registration;
    }

    /**
     * 配置XSS过滤器（针对Form内容进行安全过滤）
     * 范围：所有地址
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssFilter xssFilter) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(xssFilter);
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        /*
         * 添加不需要过滤的路径
         * Map<String, String> initParameters = Maps.newHashMap();
         * initParameters.put("excludes", "/static/*,/assets/*");
         * registration.setInitParameters(initParameters);
         */
        registration.setOrder(1);
        return registration;
    }

    /**
     * &#064;RequestBody  注解内容过滤器
     * 解析json内容，并确保不会注入Xss数据
     * 范围：所有 &#064;RequestBody注解的数据
     */
    @Bean
    public XssRequestBodyAdvice xssRequestBodyAdvice() {
        return new XssRequestBodyAdvice();
    }

    /**
     * 安全头过滤器
     * 范围：所有链接（如果是 /druid/* 路径，则不设置 X-Frame-Options）
     */
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> xssSecurityHeadersFilter(SecurityHeadersFilter securityHeadersFilter) {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(securityHeadersFilter);
        registrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息
        registrationBean.setOrder(2);
        return registrationBean;
    }

    /**
     * 配置静态资源映射
     * 配置1： 添加class目录下的static为静态资源目录
     * 配置2： 添加系统默认上传目录为资源目录（windowsBaseUploadDir/linuxBaseUploadDir）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**", "/favicon.ico").addResourceLocations("classpath:/static/");
        String uploadDir = getBaseUploadDir();
        if(StringUtils.isNotBlank(uploadDir)) {
            registry.addResourceHandler("/upload/**").addResourceLocations(uploadDir);
        }
    }

    private String getBaseUploadDir(){
        String outDir;
        if("windows".equals(ToolUtil.getOs())){
            outDir = windowsBaseUploadDir;
        }else{
            outDir = linuxBaseUploadDir;
        }
        String filePath = outDir;
        File file = new File(filePath.replace("file:",""));
        if(!file.exists() && !file.mkdirs()){
            log.error("创建基础上传目录失败");
            return null;
        }
        return outDir;
    }

    /**
     * 配置消息转换器
     * 配置方式：FastJson
     */
    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        List<MediaType> supportedMediaTypes = Lists.newArrayList();
        //设置允许的请求内容：json、xml、html、text
        supportedMediaTypes.add(MediaType.ALL);
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");    // 自定义时间格式
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullNumberAsZero,SerializerFeature.WriteNullBooleanAsFalse,SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.BrowserCompatible,SerializerFeature.WriteNonStringKeyAsString);
        fastJsonHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(fastJsonHttpMessageConverter);
        converters.add(responseBodyConverter());
    }

    /**
     * 配置字符串消息转换器
     * 配置内容：UTF_8
     */
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(
                StandardCharsets.UTF_8);
    }

    /**
     * 部署在反向代理（Reverse Proxy）或负载均衡器（Load Balancer）后面时，
     * HttpServletRequest 对象无法获取到原始客户端请求信息的问题
     * 客户端 (浏览器) <--- HTTPS ---> 反向代理 (Nginx/Apache/ELB等) <--- HTTP ---> Spring Boot 应用
     * 让应用程序能够像直接面向客户端一样获取到正确的协议、域名、端口和客户端 IP 地址
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    /**
     * 添加拦截器
     * 拦截已经登录的地址
     * 排除未登录的地址：/login，/favicon.ico ......
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login",
                        "/api/auth/refresh",
                        "/favicon.ico",
                        "/actuator/**",
                        "/error",
                        "/logout",
                        "/upload/**",
                        "/genCaptcha",
                        "/static/**",
                        "/showBlog/**",
                        "/register/**",
                        "/api/analytics/**",
                        "/druid/**");
        registry.addInterceptor(new BlogHandlerInterceptor())
                .addPathPatterns("/showBlog/**");
    }

}
