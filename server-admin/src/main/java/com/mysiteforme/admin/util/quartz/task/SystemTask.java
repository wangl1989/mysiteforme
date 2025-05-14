/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:32:31
 * @ Description: 系统定时任务（可在前端配置）
 */

package com.mysiteforme.admin.util.quartz.task;

import com.mysiteforme.admin.service.DailyStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("systemTask")
@RequiredArgsConstructor
public class SystemTask {

    private final DailyStatsService dailyStatsService;

    /**
     * 同步文章点击量
     */
    public void  synchronizationArticleView(String params){
        log.info("测试定时任务,synchronizationArticleView");
    }

    /**
     * 生成文章搜索索引
     */
    public void createArticleIndex(String params) {
        log.info("测试定时任务,createArticleIndex");
    }

    public void synchronizationDailyStas(){
        dailyStatsService.aggregateDailyStats();;
    }


}
