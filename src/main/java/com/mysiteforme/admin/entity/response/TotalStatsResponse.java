package com.mysiteforme.admin.entity.response;

import lombok.Data;

@Data
public class TotalStatsResponse {

    /**
     * 总访问量
     */
    private Integer totalTotalVisits;

    /**
     * 总独立访问量
     */
    private Integer totalUniqueVisitors;

    /**
     * 总用户数量
     */
    private Integer totalNewUsers;

    /**
     * 总点击量
     */
    private Integer totalClicks;
}
