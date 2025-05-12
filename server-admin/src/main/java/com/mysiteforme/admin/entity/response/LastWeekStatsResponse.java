package com.mysiteforme.admin.entity.response;

import lombok.Data;

@Data
public class LastWeekStatsResponse {

    /**
     * 上周平均每天的访问量
     */
    private Integer avgTotalVisits;

    /**
     * 上周平均每天独立访问量
     */
    private Integer avgUniqueVisitors;

    /**
     * 上周平均每天新用户数量
     */
    private Integer avgNewUsers;

    /**
     * 上周每天平均点击量
     */
    private Integer avgTotalClicks;
}
