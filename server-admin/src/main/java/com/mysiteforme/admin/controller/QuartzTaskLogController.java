/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:39:32
 * @ Description: 定时任务日志控制器 提供定时任务日志的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskLogRequest;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/quartzTaskLog")
@RateLimit(limit = 40, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class QuartzTaskLogController {

    private final QuartzTaskLogService quartzTaskLogService;

    public QuartzTaskLogController(QuartzTaskLogService quartzTaskLogService) {
        this.quartzTaskLogService = quartzTaskLogService;
    }

    @GetMapping("list")
    public Result list(PageListQuartzTaskLogRequest request){
        return Result.success(quartzTaskLogService.selectPageQuartzTaskLog(request));
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