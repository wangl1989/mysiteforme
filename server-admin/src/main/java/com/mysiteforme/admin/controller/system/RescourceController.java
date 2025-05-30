/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:35:39
 * @ Description: 资源控制器 提供资源的增删改查功能 
 */

package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.entity.request.PageListResourceRequest;
import com.mysiteforme.admin.util.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysiteforme.admin.service.RescourceService;
import com.mysiteforme.admin.util.Result;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/rescource")
@RequiredArgsConstructor
@RateLimit(limit = 60, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class RescourceController{

    private final RescourceService rescourceService;

    @GetMapping("list")
    public Result list(PageListResourceRequest request){
        return Result.success(rescourceService.selectPageRescource(request));
    }
}
