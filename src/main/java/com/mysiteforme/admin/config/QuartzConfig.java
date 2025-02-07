package com.mysiteforme.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Created by wangl on 2018/1/25.
 * todo:定时任务配置类
 */
@Configuration
public class QuartzConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.druid.initialSize}")
    private String initialSize;

    @Value("${spring.datasource.druid.maxActive}")
    private String maxActive;

    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setMaxActive(Integer.parseInt(maxActive));
        druidDataSource.setFilters("stat,wall,log4j");
        druidDataSource.setInitialSize(Integer.parseInt(initialSize));
        return druidDataSource;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws SQLException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource());
        //quartz参数
        Properties prop = getProperties();
        schedulerFactoryBean.setQuartzProperties(prop);

        schedulerFactoryBean.setSchedulerName("MySiteForMeScheduler");
        //延时启动
        schedulerFactoryBean.setStartupDelay(20);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        //可选，QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //设置自动启动，默认为true
        schedulerFactoryBean.setAutoStartup(true);

        return schedulerFactoryBean;
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceName", "MySiteForMeScheduler");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        //线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        //JobStore配置
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        //集群配置
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");

        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        return prop;
    }

}
