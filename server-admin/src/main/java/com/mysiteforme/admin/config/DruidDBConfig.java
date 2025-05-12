/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 20:08:11
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:30:43
 * @ Description: Druid数据源配置类 配置Druid监控统计功能
 */

package com.mysiteforme.admin.config;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
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

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        // 添加初始化参数
        registrationBean.addInitParameter("loginUsername", "admin");
        registrationBean.addInitParameter("loginPassword", "admin");
        // 是否可以重置数据
        registrationBean.addInitParameter("resetEnable", "false");
        registrationBean.addInitParameter("allowFrames", "true");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter() {
        FilterRegistrationBean<WebStatFilter> registrationBean = new FilterRegistrationBean<>(new WebStatFilter());
        // 添加过滤规则
        registrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息
        registrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid,/druid/*,/static/**,/upload/*,/register/*");
        registrationBean.addInitParameter("registrationBean","true");
        return registrationBean;
    }


}
