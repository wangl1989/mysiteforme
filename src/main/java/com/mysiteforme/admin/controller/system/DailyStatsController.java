package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.service.LogService;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.Result;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.mysiteforme.admin.service.DailyStatsService;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * 统计汇总 前端控制器
 * </p>
 *
 * @author 昵称
 * @since 2025-05-05
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dailyStats")
@RequiredArgsConstructor
public class DailyStatsController {

    private final DailyStatsService dailyStatsService;

    private final UserService userService;

    private final LogService logService;

    // 获取今日实时统计
    @GetMapping("/today")
    public Result getTodayStats() {
        return Result.success(dailyStatsService.getTodayStats());
    }

    // 获取N天之前到现在的每天访问数据
    @GetMapping("/day/trend")
    public Result getTrendData(@RequestParam(defaultValue = "9") int days) {
        return Result.success(dailyStatsService.getTrendData(days));
    }

    // 获取年度访问趋势数据
    @GetMapping("/month/trend")
    public Result getTrendData() {
        return Result.success(dailyStatsService.getYearTrendStatsDataResponse());
    }

    // 获取首页用户数据集合
    @GetMapping("/user/list")
    public Result getUserList(@RequestParam(defaultValue = "6")int limit){
        return Result.success(userService.getAnalyticsUserResponseList(limit));
    }

    // 首页获取操作日志
    @GetMapping("/log/list")
    public Result getLogList(@RequestParam(defaultValue = "6")int limit){
        return Result.success(logService.getIndexLogList(limit));
    }

    // 首页获取用户系统相关数据
    @GetMapping("/user/systemData")
    public Result getUserSystemData(){
        Long userId = MySecurityUser.id();
        if(userId == null){
            return Result.unauthorized();
        }
        return Result.success(dailyStatsService.getUserSystemData(userId));
    }

    // 手动触发统计汇总（仅用于测试）
    @PostMapping("/aggregate")
    public Result triggerAggregation() {
        dailyStatsService.aggregateDailyStats();
        return Result.success();
    }

}
