package com.mysiteforme.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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


}
