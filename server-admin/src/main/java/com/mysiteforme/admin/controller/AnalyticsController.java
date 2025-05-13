package com.mysiteforme.admin.controller;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.entity.VisitLogs;
import com.mysiteforme.admin.entity.request.AddClickEventsRequest;
import com.mysiteforme.admin.entity.request.AddVisitLogsRequest;
import com.mysiteforme.admin.service.VisitLogsService;
import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VisitLogsService visitLogsService;
    @RateLimit(period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.IP)
    @PostMapping("visitLogs")
    public Result visitLogs(@RequestBody @Valid AddVisitLogsRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        VisitLogs visitLogs = new VisitLogs();
        BeanUtils.copyProperties(request,visitLogs);
        visitLogsService.createVisitLogs(visitLogs);
        return Result.success();
    }

    @RateLimit(period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.IP)
    @PostMapping("batchEventLogs")
    public Result clickEvents(@RequestBody AddClickEventsRequest request){
        if(request == null){
            return Result.objectNotNull();
        }
        if(CollectionUtils.isEmpty(request.getEvents())){
            return Result.businessMsgError(MessageConstants.User.CLICK_EVENT_EMPTY);
        }
        visitLogsService.createClickEvents(request);
        return Result.success();
    }
}
