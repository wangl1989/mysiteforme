package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.dao.QuartzTaskDao;
import com.mysiteforme.admin.service.QuartzTaskService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.quartz.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * <p>
 * 定时任务 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QuartzTaskServiceImpl extends ServiceImpl<QuartzTaskDao, QuartzTask> implements QuartzTaskService {

    private Scheduler scheduler ;

    public QuartzTaskServiceImpl() {
        super();
    }

    @Autowired
    public QuartzTaskServiceImpl(QuartzTaskDao baseMapper,Scheduler scheduler) {
        this.baseMapper = baseMapper;
        this.scheduler = scheduler;
    }

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init(){
        QueryWrapper<QuartzTask> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        List<QuartzTask> scheduleJobList = list(wrapper);
        for(QuartzTask scheduleJob : scheduleJobList){
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
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
        ScheduleUtils.createScheduleJob(scheduler,quartzTask);
    }

    /**
     * 更新定时任务
     * 同时更新调度任务
     * @param quartzTask 任务对象
     */
    @Override
    public void updateQuartzTask(QuartzTask quartzTask) {
        baseMapper.updateById(quartzTask);
        ScheduleUtils.updateScheduleJob(scheduler,quartzTask);
    }

    /**
     * 批量删除定时任务
     * @param ids 任务ID列表
     */
    @Override
    public void deleteBatchTasks(List<Long> ids) {
        for(Long id : ids){
            ScheduleUtils.deleteScheduleJob(scheduler, id);
        }
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
        for(Long jobId : jobIds){
            ScheduleUtils.run(scheduler, queryObject(jobId));
        }
    }

    /**
     * 暂停多个任务
     * @param jobIds 任务ID列表
     */
    @Override
    public void paush(List<Long> jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.pauseJob(scheduler, jobId);
        }
        updateBatchTasksByStatus(jobIds, Constants.QUARTZ_STATUS_PUSH);
    }

    /**
     * 恢复多个任务
     * @param jobIds 任务ID列表
     */
    @Override
    public void resume(List<Long> jobIds) {
        for(Long jobId : jobIds){
            ScheduleUtils.resumeJob(scheduler, jobId);
        }

        updateBatchTasksByStatus(jobIds, Constants.QUARTZ_STATUS_NOMAL);
    }
}
