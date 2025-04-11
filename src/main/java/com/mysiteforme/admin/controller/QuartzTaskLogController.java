/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:39:32
 * @ Description: 定时任务日志控制器 提供定时任务日志的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskLogRequest;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.RestResponse;

import com.mysiteforme.admin.util.Result;
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

import java.util.Map;


@RestController
@RequestMapping("/api/admin/quartzTaskLog")
public class QuartzTaskLogController {

    private final QuartzTaskLogService quartzTaskLogService;

    public QuartzTaskLogController(QuartzTaskLogService quartzTaskLogService) {
        this.quartzTaskLogService = quartzTaskLogService;
    }

    @GetMapping("list")
    public Result list(@RequestBody PageListQuartzTaskLogRequest request){
        return Result.success(quartzTaskLogService.selectPageQuartzTaskLog(request));
    }

    @PostMapping("add")
    public Result add(@RequestBody QuartzTaskLog quartzTaskLog){
        quartzTaskLogService.save(quartzTaskLog);
        return Result.success();
    }

    @PostMapping("edit")
    public Result edit(@RequestBody QuartzTaskLog quartzTaskLog){
        if(null == quartzTaskLog.getId() || 0 == quartzTaskLog.getId()){
            return Result.idIsNullError();
        }
        quartzTaskLogService.updateById(quartzTaskLog);
        return Result.success();
    }

    @DeleteMapping("delete")
    @SysLog(MessageConstants.SysLog.QUARTZ_TASK_LOG_DELETE)
    public Result delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return Result.idIsNullError();
        }
        QuartzTaskLog quartzTaskLog = quartzTaskLogService.getById(id);
        quartzTaskLog.setDelFlag(true);
        quartzTaskLogService.updateById(quartzTaskLog);
        return Result.success();
    }

}