package com.mysiteforme.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Quartz定时任务配置类
 * 配置定时任务的执行器和持久化
 * @author wangl
 * @since 2018/1/25
 */
@Configuration
public class QuartzConfig {


    private DataSource dataSource;

    public QuartzConfig() {}

    @Autowired
    public QuartzConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 配置Quartz调度工厂
     * @return SchedulerFactoryBean实例
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws SQLException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
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
