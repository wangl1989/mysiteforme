/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:57
 * @ Description: 定时任务Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.QuartzTask;

import java.util.List;

public interface QuartzTaskService extends IService<QuartzTask> {

    /**
     * 根据ID，查询定时任务
     */
    QuartzTask queryObject(Long jobId);

    /**
     * 分页查询定时任务列表
     */
    IPage<QuartzTask> queryList(QueryWrapper<QuartzTask> wrapper, IPage<QuartzTask> page);

    /**
     * 保存定时任务
     */
    void saveQuartzTask(QuartzTask quartzTask);

    /**
     * 更新定时任务
     */
    void updateQuartzTask(QuartzTask quartzTask);

    /**
     * 批量删除定时任务
     */
    void deleteBatchTasks(List<Long>  ids);

    /**
     * 批量更新定时任务状态
     */
    void updateBatchTasksByStatus(List<Long> ids, Integer status);

    /**
     * 立即执行
     */
    void run(List<Long> jobIds);

    /**
     * 暂停运行
     */
    void paush(List<Long> jobIds);

    /**
     * 恢复运行
     */
    void resume(List<Long> jobIds);
}
