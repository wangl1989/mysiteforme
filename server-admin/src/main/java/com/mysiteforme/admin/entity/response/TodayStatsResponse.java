package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TodayStatsResponse implements Serializable {

    /**
     * 今天的访问量
     */
    private Integer totalVisits;

    /**
     * 今天的独立访客
     */
    private Integer uniqueVisitors;

    /**
     * 今天的新访客
     */
    private Integer newUsers;

    /**
     * 今天的总共点击数量
     */
    private Integer totalClicks;

    /**
     * 今日平均访问时长
     */
    private Integer avgVisitDuration;

    /**
     * 计算跳出率
     */
    private Integer bounceRate;

    /**
     * 访问总数
     */
    private Integer allTotalVisits;

    /**
     * 点击总数
     */
    private Integer allTotalClicks;

    /**
     * 总用户数量
     */
    private Integer allTotalUser;

    /**
     * 访问比较上周百分比
     */
    private Integer visitsPercent;

    /**
     * 独立访客比较上周百分比
     */
    private Integer visitorsPercent;

    /**
     * 点击量比较上周百分比
     */
    private Integer clickPercent;

    /**
     * 新用户较上周百分比
     */
    private Integer newUserPercent;
}
