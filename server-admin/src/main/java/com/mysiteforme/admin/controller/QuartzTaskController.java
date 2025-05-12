/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:39:20
 * @ Description: 定时任务控制器 提供定时任务的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.mysiteforme.admin.entity.request.AddQuartzTaskRequest;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskRequest;
import com.mysiteforme.admin.entity.request.UpdateQuartzTaskRequest;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.service.QuartzTaskService;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/quartzTask")
public class QuartzTaskController {

    private final QuartzTaskService quartzTaskService;

    public QuartzTaskController(QuartzTaskService quartzTaskService) {
        this.quartzTaskService = quartzTaskService;
    }

    /**
     * 获取任务列表数据
     * @return 分页数据
     */
    @GetMapping("list")
    public Result list(PageListQuartzTaskRequest request){
        return Result.success(quartzTaskService.selectPageQuartzTask(request));
    }

    /**
     * 保存任务
     * @param request 任务对象
     * @return 操作结果
     */
    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_ADD)
    public Result add(@RequestBody @Valid AddQuartzTaskRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        QuartzTask quartzTask = new QuartzTask();
        BeanUtils.copyProperties(request,quartzTask);
        quartzTaskService.saveQuartzTask(quartzTask);
        return Result.success();
    }

    /**
     * 更新任务
     * @param request 任务对象
     * @return 操作结果
     */
    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_EDIT)
    public Result edit(@RequestBody @Valid UpdateQuartzTaskRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(null == request.getId() || 0 == request.getId()){
            return Result.idIsNullError();
        }
        QuartzTask quartzTask = new QuartzTask();
        BeanUtils.copyProperties(request,quartzTask);
        quartzTaskService.updateQuartzTask(quartzTask);
        return Result.success();
    }

    /**
     * 删除任务
     * @param ids 任务ID集合
     * @return 操作结果
     */
    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_DELETE)
    public Result delete(@RequestParam(value = "ids",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return Result.idIsNullError();
        }
        quartzTaskService.deleteBatchTasks(ids);
        return Result.success();
    }

    /**
     * 暂停选中的定时任务
     * @param ids 任务ID List
     */
    @PostMapping("paush")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_PAUSH)
    public Result paush(@RequestParam(value = "ids",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return Result.idIsNullError();
        }
        quartzTaskService.paush(ids);
        return Result.success();
    }

    /**
     * 恢复选中的定时任务运行
     * @param ids 任务ID List
     */
    @PostMapping("resume")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_RESUME)
    public Result resume(@RequestParam(value = "ids",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return Result.idIsNullError();
        }
        quartzTaskService.resume(ids);
        return Result.success();
    }

    /**
     * 立即执行选中的定时任务
     * @param ids 任务ID List
     */
    @PostMapping("run")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_RUN)
    public Result run(@RequestParam(value = "ids",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return Result.idIsNullError();
        }
        quartzTaskService.run(ids);
        return Result.success();
    }

}