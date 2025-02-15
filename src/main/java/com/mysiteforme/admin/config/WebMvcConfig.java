/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:14:52
 * @ Description: WebMvc配置类 配置视图解析、资源映射、消息转换等
 */

package com.mysiteforme.admin.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.MultipartConfigElement;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.base.BlogHandlerInterceptor;
import com.mysiteforme.admin.base.MyHandlerInterceptor;
import com.mysiteforme.admin.security.SecurityHeadersFilter;
import com.mysiteforme.admin.security.XssFilter;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }

    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

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
    public FilterRegistrationBean<SecurityHeadersFilter> xssSecurityHeadersFilter(SecurityHeadersFilter securityHeadersFilter) {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(securityHeadersFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    /**
     * 配置静态资源映射
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
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
     * 配置文件上传大小限制
     * @return 文件上传配置
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制 ,超出设置页面会抛出异常信息，
        // 这样在文件上传的地方就需要进行异常信息的处理了;
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        // Sets the directory location where files will be stored.
        // factory.setLocation("路径地址");
        return factory.createMultipartConfig();
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
                .excludePathPatterns("/login","/login/main","/logout","/genCaptcha","/static/**","/showBlog/**");
        registry.addInterceptor(new BlogHandlerInterceptor())
                .addPathPatterns("/showBlog/**");
    }

}
