package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.DailyStats;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.response.IndexSystemDataResponse;
import com.mysiteforme.admin.entity.response.TodayStatsResponse;
import com.mysiteforme.admin.entity.response.TrendStatsDataResponse;
import com.mysiteforme.admin.entity.response.YearTrendStatsDataResponse;

/**
 * <p>
 * 统计汇总 服务类
 * </p>
 *
 * @author 昵称
 * @since 2025-05-05
 */
public interface DailyStatsService extends IService<DailyStats> {

    // 获取今日实时统计数据
    TodayStatsResponse getTodayStats();

    // 获取N天之前到现在的每天访问数据
    TrendStatsDataResponse getTrendData(int days);

    // 获取年度访问量统计
    YearTrendStatsDataResponse getYearTrendStatsDataResponse();

    // 每日统计数据汇总（定时任务调用）
    void aggregateDailyStats();

    // 获取首页用户基本数据
    IndexSystemDataResponse getUserSystemData(Long userId);


}
