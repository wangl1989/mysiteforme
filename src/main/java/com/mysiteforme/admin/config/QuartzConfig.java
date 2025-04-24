/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 20:33:34
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 20:49:04
 * @ Description: Quartz定时任务配置类 配置定时任务的执行器和持久化
 */

package com.mysiteforme.admin.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
public class QuartzConfig {

    private final DataSource dataSource;

    public QuartzConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 配置Quartz调度工厂
     * @return SchedulerFactoryBean实例
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        schedulerFactoryBean.setDataSource(dataSource);
        //quartz参数
        Properties prop = getProperties();
        schedulerFactoryBean.setQuartzProperties(prop);

        schedulerFactoryBean.setSchedulerName("MySiteForMeScheduler");
        //延时启动
        schedulerFactoryBean.setStartupDelay(5);
        //可选(覆盖已存在的任务)，QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //设置自动启动，默认为true
        schedulerFactoryBean.setAutoStartup(true);

        return schedulerFactoryBean;
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        //线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "10");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        //JobStore配置
        prop.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        //集群配置
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "10000");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");

        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        prop.put("org.quartz.jobStore.tablePrefix", "qrtz_");
        return prop;
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        return schedulerFactoryBean().getScheduler();
    }

}
