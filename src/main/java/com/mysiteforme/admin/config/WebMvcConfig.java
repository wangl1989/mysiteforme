package com.mysiteforme.admin.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.base.BlogHandlerInterceptor;
import com.mysiteforme.admin.base.MyHandlerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring MVC配置类
 * 配置视图解析、资源映射、消息转换等
 * @author wang
 * @since 2025/02/07
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    /**
//     * 添加视图控制器
//     * @param registry 视图控制器注册器
//     */
//    @Override
//    public void addViewControllers(@NotNull ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("login");  // 将 `/login` 映射到 `login.html`
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);              // 优先级（可选）
//    }

//    /**
//     * RequestContextListener注册
//     */
//    @Bean
//    public ServletListenerRegistrationBean<RequestContextListener> requestContextListenerRegistration() {
//        return new ServletListenerRegistrationBean<>(new RequestContextListener());
//    }

//    @Bean
//    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
//        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new XssFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("xssFilter");
//        registration.setOrder(1);
//        return registration;
//    }
//
//    @Bean
//    public FilterRegistrationBean<SecurityHeadersFilter> xssSecurityHeadersFilter() {
//        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new SecurityHeadersFilter());
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setOrder(2);
//        return registrationBean;
//    }

    /**
     * 配置静态资源映射
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

//    //跨域配置（CORS）
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")                   // 允许跨域的路径
////                .allowedOrigins("https://example.com")    // 允许的来源
//                .allowedMethods("GET", "POST")           // 允许的HTTP方法
//                .allowCredentials(true);                 // 允许携带凭证（如Cookie）
//    }

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
