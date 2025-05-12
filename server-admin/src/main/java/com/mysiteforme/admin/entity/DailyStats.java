package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import java.io.Serial;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 *
 * 统计汇总
 * </p>
 *
 * @author 昵称
 * @since 2025-05-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("analytics_daily_stats")
public class DailyStats extends DataEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 统计日期
     */
    @TableField("stat_date")
    private LocalDate statDate;
    /**
     * 总访问量
     */
    @TableField("total_visits")
    private Integer totalVisits;
    /**
     * 独立访客数
     */
    @TableField("unique_visitors")
    private Integer uniqueVisitors;
    /**
     * 新用户
     */
    @TableField("new_users")
    private Integer newUsers;
    /**
     * 总点击量
     */
    @TableField("total_clicks")
    private Integer totalClicks;
    /**
     * 平均访问时长(秒)
     */
    @TableField("avg_visit_duration")
    private Integer avgVisitDuration;


}