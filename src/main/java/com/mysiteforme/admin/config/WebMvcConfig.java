/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 21:09:57
 * @ Description: WebMvc配置类 配置视图解析、资源映射、消息转换等
 */

package com.mysiteforme.admin.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
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
import com.mysiteforme.admin.util.ApiToolUtil;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ResultCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomExceptionHandlerFilter filter;

    private final ApiToolUtil apiToolUtil;

    public WebMvcConfig(CustomExceptionHandlerFilter filter,ApiToolUtil apiToolUtil) {
        this.filter = filter;
        this.apiToolUtil = apiToolUtil;
    }

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

    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }

    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    /**
     * 配置异常处理过滤器
     * @return 异常处理过滤器注册器
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
     * 配置XSS过滤器
     * @param xssFilter XSS过滤器
     * @return XSS过滤器注册器
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

    @Bean
    public XssRequestBodyAdvice xssRequestBodyAdvice() {
        return new XssRequestBodyAdvice();
    }

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
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**","/favicon.ico").addResourceLocations("classpath:/static/");
    }

    /**
     * 配置消息转换器
     * @param converters 消息转换器列表
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
     * @return 字符串消息转换器
     */
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(
                StandardCharsets.UTF_8);
    }

    /**
     * 添加拦截器
     * @param registry 拦截器注册器
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
                        "/genCaptcha",
                        "/static/**",
                        "/showBlog/**",
                        "/druid/**");
        registry.addInterceptor(new BlogHandlerInterceptor())
                .addPathPatterns("/showBlog/**");
    }

}
