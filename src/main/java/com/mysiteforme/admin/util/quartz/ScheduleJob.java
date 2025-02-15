/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:32:44
 * @ Description: 定时任务调度
 */

package com.mysiteforme.admin.util.quartz;

import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;


@Component
public class ScheduleJob extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(ScheduleJob.class);
	private final ExecutorService service = Executors.newSingleThreadExecutor();

	private QuartzTaskLogService quartzTaskLogService;

	public ScheduleJob() {}

	@Autowired
	public ScheduleJob(QuartzTaskLogService quartzTaskLogService){
		this.quartzTaskLogService = quartzTaskLogService;
	}

    @Override
    protected void executeInternal(JobExecutionContext context) {
        QuartzTask scheduleJob = (QuartzTask) context.getMergedJobDataMap().get(QuartzTask.JOB_PARAM_KEY);
        String param = scheduleJob.getParams();
        //数据库保存执行记录
        QuartzTaskLog log = new QuartzTaskLog();
        log.setJobId(scheduleJob.getId());
        log.setTargetBean(scheduleJob.getTargetBean());
        log.setTrgetMethod(scheduleJob.getTrgetMethod());
        log.setParams(param);
        log.setName("执行定时任务【"+scheduleJob.getName()+"】");
        if(StringUtils.isNotBlank(param) && StringUtils.isNumeric(param)){
			log.setCreateId(Long.valueOf(param));
			log.setUpdateId(Long.valueOf(param));
		}else{
        	//定义死
			log.setCreateId(1L);
			log.setUpdateId(1L);
		}
        log.setCreateDate(new Date());
        //任务开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            //执行任务
        	logger.info("任务准备执行，任务ID：{}" , scheduleJob.getId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getTargetBean(),
            		scheduleJob.getTrgetMethod(), scheduleJob.getParams());
            Future<?> future = service.submit(task);
            
			future.get();
			
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.setTimes((int)times);
			//任务状态    0：成功    1：失败
			log.setStatus(0);

            logger.info("任务执行完毕，任务ID：{}  总共耗时：{}毫秒", scheduleJob.getId(), times);
		} catch (InterruptedException | ExecutionException | NoSuchMethodException e) {
            logger.error("任务执行失败，任务ID：{}", scheduleJob.getId(), e);
			
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.setTimes((int)times);

			//任务状态    0：成功    1：失败
			log.setStatus(1);
			log.setError(e.getMessage());
		}finally {
            logger.error("任务执行结束，任务ID：{}", scheduleJob.getId());
			quartzTaskLogService.save(log);
		}
    }
}
