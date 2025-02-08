package com.mysiteforme.admin.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.sql.DataSource;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.SQLException;

/**
 * Druid数据源配置类
 * 配置Druid监控统计功能
 */
@Configuration
public class DruidDBConfig {

    @Value("${spring.datasource.druid.username}")
    private String username;

    @Value("${spring.datasource.druid.password}")
    private String password;

    @Value("${spring.datasource.druid.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.druid.url}")
    private String url;

    @Value("${spring.datasource.druid.initialSize}")
    private String initialSize;

    @Value("${spring.datasource.druid.maxActive}")
    private String maxActive;

    /**
     * 配置Druid数据源
     * @return DruidDataSource实例
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setMaxActive(Integer.parseInt(maxActive));
        druidDataSource.setFilters("stat,wall,slf4j");
        druidDataSource.setInitialSize(Integer.parseInt(initialSize));
        return druidDataSource;
    }

    /**
     * 配置Druid监控Servlet
     * @return ServletRegistrationBean实例
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        //reg.addInitParameter("allow", "127.0.0.1"); //白名单
        reg.addInitParameter("resetEnable","false");
        return reg;
    }

    /**
     * 配置Druid监控Filter
     * @return FilterRegistrationBean实例
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        System.out.println("Registering Druid WebStatFilter");
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter(){
            @Override
            public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response, javax.servlet.FilterChain chain)
                    throws java.io.IOException, javax.servlet.ServletException {
                System.out.println("Druid WebStatFilter 拦截URL地址为: " + ((javax.servlet.http.HttpServletRequest) request).getRequestURI());
                super.doFilter(request, response, chain);
            }
        });
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,*.json,*.sql,*.html,/login,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName","USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName","");
        filterRegistrationBean.addInitParameter("aopPatterns","com.mysiteforme.admin.service");

        return filterRegistrationBean;
    }
}
