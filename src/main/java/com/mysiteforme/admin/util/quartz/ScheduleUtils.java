package com.mysiteforme.admin.util.quartz;

import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.Constants;
import org.quartz.*;

import java.util.Objects;


/**
 * 类ScheduleUtils的功能描述:
 * 定时任务工具类
 * &#064;author  hxy
 * &#064;date  2017-08-25 16:18:10
 */
public class ScheduleUtils {
    private final static String JOB_NAME = "TASK_";
    
    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }
    
    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("获取定时任务CronTrigger出现异常").build();
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, QuartzTask scheduleJob) {
        try {
        	//构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getId())).build();

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
            		.withMisfireHandlingInstructionDoNothing();

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getId())).withSchedule(scheduleBuilder).build();

            //放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(QuartzTask.JOB_PARAM_KEY, scheduleJob);
            
            scheduler.scheduleJob(jobDetail, trigger);
            
            //暂停任务
            if(Objects.equals(scheduleJob.getStatus(), Constants.QUARTZ_STATUS_PUSH)){
            	pauseJob(scheduler, scheduleJob.getId());
            }
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("创建定时任务失败").build();
        }
    }
    
    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, QuartzTask scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getId());

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
            		.withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getId());
            
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            
            //参数
            trigger.getJobDataMap().put(QuartzTask.JOB_PARAM_KEY, scheduleJob);
            
            scheduler.rescheduleJob(triggerKey, trigger);
            
            //暂停任务
            if(Objects.equals(scheduleJob.getStatus(), Constants.QUARTZ_STATUS_PUSH)){
            	pauseJob(scheduler, scheduleJob.getId());
            }
            
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("更新定时任务失败").throwable(e).build();
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, QuartzTask scheduleJob) {
        try {
        	//参数
        	JobDataMap dataMap = new JobDataMap();
        	dataMap.put(QuartzTask.JOB_PARAM_KEY, scheduleJob);
        	
            scheduler.triggerJob(getJobKey(scheduleJob.getId()), dataMap);
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("立即执行定时任务失败").throwable(e).build();
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("暂停定时任务失败").throwable(e).build();
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("暂停定时任务失败").throwable(e).build();
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("删除定时任务失败").throwable(e).build();
        }
    }
}
