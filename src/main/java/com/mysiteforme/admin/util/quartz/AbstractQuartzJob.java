package com.mysiteforme.admin.util.quartz;

import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractQuartzJob extends QuartzJobBean {

    private QuartzTaskLog quartzTaskLog;

    private QuartzTaskLogService quartzTaskLogService;

    public AbstractQuartzJob(){

    }

    @Autowired
    public AbstractQuartzJob(QuartzTaskLogService quartzTaskLogService){
        this.quartzTaskLogService = quartzTaskLogService;
    }

    public static ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        try {
            beforeExecute(context);
            doExecute(context,quartzTaskLog);
            afterExecute(context, null);
        } catch (Exception e) {
            afterExecute(context, e);
            throw new JobExecutionException(e);
        }
    }

    /**
     * 执行前
     */
    protected void beforeExecute(JobExecutionContext context) {
        startTime.set(System.currentTimeMillis());
        QuartzTask scheduleJob = (QuartzTask) context.getMergedJobDataMap().get(Constants.JOB_PARAM_KEY);
        // 可以记录任务开始日志
        //数据库保存执行记录
        quartzTaskLog = new QuartzTaskLog();
        quartzTaskLog.setJobId(scheduleJob.getId());
        quartzTaskLog.setCron(scheduleJob.getCron());
        quartzTaskLog.setGroupName(scheduleJob.getGroupName());
        quartzTaskLog.setTargetBean(scheduleJob.getTargetBean());
        quartzTaskLog.setTrgetMethod(scheduleJob.getTrgetMethod());
        quartzTaskLog.setParams(scheduleJob.getParams());
        quartzTaskLog.setName("执行定时任务【"+scheduleJob.getName()+"】");
        quartzTaskLog.setCreateId(1L);
        quartzTaskLog.setUpdateId(1L);
        //任务开始时间
    }

    /**
     * 执行后
     */
    protected void afterExecute(JobExecutionContext context, Exception e) {
        // 可以记录任务结束日志或异常
        quartzTaskLog.setTimes(Long.valueOf(System.currentTimeMillis() - startTime.get()).intValue());
        if(quartzTaskLogService == null){
            ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
            quartzTaskLogService = applicationContext.getBean(QuartzTaskLogService.class);
        }
        quartzTaskLogService.save(quartzTaskLog);
    }

    /**
     * 执行方法，由子类重写
     */
    protected abstract void doExecute(JobExecutionContext context,QuartzTaskLog quartzTaskLog) throws Exception;
}


