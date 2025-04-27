/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:33:37
 * @ Description: 日志控制器 提供日志的查询功能
 */

package com.mysiteforme.admin.controller.system;

import java.util.List;

import com.mysiteforme.admin.entity.request.PageListSystemLogRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.mysiteforme.admin.service.LogService;
import com.mysiteforme.admin.util.Result;

import lombok.RequiredArgsConstructor;


@Slf4j
@RestController
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
public class LogController{

    private final LogService logService;

    @GetMapping("list")
    public Result list(PageListSystemLogRequest request){
        return  Result.success(logService.selectPageLogs(request));
    }

    @DeleteMapping("delete")
    public Result delete(@RequestParam(value = "ids",required = false)List<Integer> ids){
        if(ids == null || ids.isEmpty()){
            return Result.idIsNullError();
        }
        logService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping("pvs")
    public Result getPV(){
        List<Integer> pvs = logService.selectSelfMonthData();
        return Result.success(pvs);
    }
}
