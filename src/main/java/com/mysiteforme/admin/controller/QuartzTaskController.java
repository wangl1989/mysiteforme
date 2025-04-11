/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:39:20
 * @ Description: 定时任务控制器 提供定时任务的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskRequest;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.service.QuartzTaskService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;

import jakarta.servlet.ServletRequest;

import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.List;
import java.util.Map;

@Controller
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
    public Result list(@RequestBody PageListQuartzTaskRequest request){
        return Result.success(quartzTaskService.selectPageQuartzTask(request));
    }

    /**
     * 保存任务
     * @param quartzTask 任务对象
     * @return 操作结果
     */
    @PostMapping("add")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_ADD)
    public Result add(@RequestBody QuartzTask quartzTask){
        quartzTaskService.saveQuartzTask(quartzTask);
        return Result.success();
    }

    /**
     * 更新任务
     * @param quartzTask 任务对象
     * @return 操作结果
     */
    @PutMapping("edit")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_EDIT)
    public Result edit(@RequestBody QuartzTask quartzTask){
        if(null == quartzTask.getId() || 0 == quartzTask.getId()){
            return Result.idIsNullError();
        }
        quartzTaskService.updateQuartzTask(quartzTask);
        return Result.success();
    }

    /**
     * 删除任务
     * @param ids 任务ID集合
     * @return 操作结果
     */
    @DeleteMapping("delete")
    @ResponseBody
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_DELETE)
    public Result delete(@RequestParam(value = "ids[]",required = false)List<Long> ids){
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
    @ResponseBody
    public Result paush(@RequestParam(value = "ids[]",required = false)List<Long> ids){
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
    @ResponseBody
    public Result resume(@RequestParam(value = "ids[]",required = false)List<Long> ids){
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
    @ResponseBody
    public Result run(@RequestParam(value = "ids[]",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return Result.idIsNullError();
        }
        quartzTaskService.run(ids);
        return Result.success();
    }

}