/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:57
 * @ Description: 定时任务服务实现类 提供定时任务的业务逻辑处理 
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.dao.QuartzTaskDao;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.QuartzTaskService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.JobStatus;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.quartz.QuartzJobExecution;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class QuartzTaskServiceImpl extends ServiceImpl<QuartzTaskDao, QuartzTask> implements QuartzTaskService {

    private final Scheduler scheduler ;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() throws Exception {
        List<QuartzTask> jobList = lambdaQuery()
                .eq(QuartzTask::getDelFlag,false)
                .eq(QuartzTask::getStatus,JobStatus.NORMAL.getValue())
                .list();
        for (QuartzTask job : jobList) {
            scheduleJob(job);
        }
    }

    /**
     * 增加一个job
     *
     * @param quartzTask 任务对象
     */
    public void scheduleJob(QuartzTask quartzTask) {
        try {
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobExecution.class)
                    .withIdentity(quartzTask.getName(), quartzTask.getGroupName())
                    .withDescription(quartzTask.getRemarks())
                    .build();
            // 表达式调度构建器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartzTask.getCron());

            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(quartzTask.getName(), quartzTask.getGroupName())
                    .withSchedule(cronScheduleBuilder)
                    .build();

            // 放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(Constants.JOB_PARAM_KEY, quartzTask);

            // 判断是否存在
            JobKey jobKey = JobKey.jobKey(quartzTask.getName(), quartzTask.getGroupName());

            if (scheduler.checkExists(jobKey)) {
                // 防止创建时存在数据问题，先移除，然后在执行创建操作
                scheduler.deleteJob(jobKey);
            }
            scheduler.scheduleJob(jobDetail,trigger);
            // 暂停任务
            if (Objects.equals(quartzTask.getStatus(), JobStatus.PAUSED.getValue())) {
                scheduler.pauseJob(jobKey);
            }
        } catch (SchedulerException e) {
            log.error("[增加一个job]创建定时任务失败:{}",e.getMessage());
            quartzTask.setStatus(JobStatus.ERROR.getValue());
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
        }
    }

    @Override
    public IPage<QuartzTask> selectPageQuartzTask(PageListQuartzTaskRequest request) {
        LambdaQueryWrapper<QuartzTask> wrapper = new LambdaQueryWrapper<>();
        if(request != null){
            if(StringUtils.isNotBlank(request.getName())) {
                wrapper.like(QuartzTask::getName, request.getName());
            }
            if(request.getStatus() != null){
                wrapper.eq(QuartzTask::getStatus,request.getStatus());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), QuartzTask::getCreateDate);
        }else{
            request = new PageListQuartzTaskRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    /**
     * 根据任务ID查询任务对象
     * @param jobId 任务ID
     * @return 任务对象
     */
    @Override
    public QuartzTask queryObject(Long jobId) {
        return baseMapper.selectById(jobId);
    }

    /**
     * 分页查询任务列表
     * @param wrapper 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    @Override
    public IPage<QuartzTask> queryList(QueryWrapper<QuartzTask> wrapper, IPage<QuartzTask> page) {
        return page(page,wrapper);
    }

    /**
     * 保存定时任务
     * 同时创建调度任务
     * @param quartzTask 任务对象
     */
    @Override
    public void saveQuartzTask(QuartzTask quartzTask) {
        baseMapper.insert(quartzTask);
        try {
            JobKey jobKey = JobKey.jobKey(quartzTask.getName(), quartzTask.getGroupName());
            if (scheduler.checkExists(jobKey)) {
                throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_NAME_EXISTS).build();
            }
        } catch (SchedulerException e) {
            log.error("[保存定时任务]创建定时任务失败:{}",e.getMessage());
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
        }
        scheduleJob(quartzTask);
    }

    /**
     * 更新定时任务
     * 同时更新调度任务
     * @param quartzTask 任务对象
     */
    @Override
    public void updateQuartzTask(QuartzTask quartzTask) {
        QuartzTask oldTask = baseMapper.selectById(quartzTask.getId());
        if(oldTask == null){
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_NOT_FOUND).build();
        }
        baseMapper.updateById(quartzTask);
        try {
            JobKey jobKey = JobKey.jobKey(quartzTask.getName(), quartzTask.getGroupName());
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            log.error("[更新定时任务]定时任务失败:{}",e.getMessage());
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
        }
        scheduleJob(quartzTask);
    }

    /**
     * 批量删除定时任务
     * @param ids 任务ID列表
     */
    @Override
    public void deleteBatchTasks(List<Long> ids) {
        List<QuartzTask> list = listByIds(ids);
        list.forEach(q -> {
            JobKey jobKey = JobKey.jobKey(q.getName(), q.getGroupName());
            try {
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                log.error("[删除定时任务]定时任务失败:{}",e.getMessage());
                throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
            }
        });
        removeByIds(ids);
    }

    /**
     * 批量更新任务状态
     * @param ids 任务ID列表
     * @param status 目标状态
     */
    @Override
    public void updateBatchTasksByStatus(List<Long> ids, Integer status) {
        List<QuartzTask> list = listByIds(ids);
        for (QuartzTask task : list){
            task.setStatus(status);
        }
        updateBatchById(list);
    }

    /**
     * 立即执行多个任务
     * @param jobIds 任务ID列表
     */
    @Override
    public void run(List<Long> jobIds) {
        List<QuartzTask> list = listByIds(jobIds);
        list.forEach(q -> {
            JobKey jobKey = JobKey.jobKey(q.getName(), q.getGroupName());
            try {
                if (!scheduler.checkExists(jobKey)) {
                    scheduleJob(q);
                }
                scheduler.triggerJob(jobKey);
            } catch (SchedulerException e) {
                log.error("[立即执行定时任务]定时任务失败:{}",e.getMessage());
                throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
            }
        });
    }

    /**
     * 暂停多个任务
     * @param jobIds 任务ID列表
     */
    @Override
    public void paush(List<Long> jobIds) {
        List<QuartzTask> list = listByIds(jobIds);
        list.forEach(q -> {
            JobKey jobKey = JobKey.jobKey(q.getName(), q.getGroupName());
            try {
                if (scheduler.checkExists(jobKey)) {
                    scheduler.pauseJob(jobKey);
                }
            } catch (SchedulerException e) {
                log.error("[暂停定时任务]定时任务失败:{}",e.getMessage());
                throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
            }
        });
        updateBatchTasksByStatus(jobIds, JobStatus.PAUSED.getValue());
    }

    /**
     * 恢复多个任务
     * @param jobIds 任务ID列表
     */
    @Override
    public void resume(List<Long> jobIds) {
        List<QuartzTask> list = listByIds(jobIds);
        list.forEach(q -> {
            JobKey jobKey = JobKey.jobKey(q.getName(), q.getGroupName());
            try {
                if (scheduler.checkExists(jobKey)) {
                    scheduler.resumeJob(jobKey);
                }
            } catch (SchedulerException e) {
                log.error("[恢复多个任务]定时任务失败:{}",e.getMessage());
                throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXCEPTION).build();
            }
        });

        updateBatchTasksByStatus(jobIds, Constants.QUARTZ_STATUS_NOMAL);
    }
}
