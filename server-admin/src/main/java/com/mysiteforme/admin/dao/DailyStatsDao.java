package com.mysiteforme.admin.dao;

import com.mysiteforme.admin.entity.DTO.YearTrendStatsDTO;
import com.mysiteforme.admin.entity.DailyStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.response.LastWeekStatsResponse;
import com.mysiteforme.admin.entity.response.TotalStatsResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 统计汇总 Mapper 接口
 * </p>
 *
 * @author 昵称
 * @since 2025-05-05
 */

public interface DailyStatsDao extends BaseMapper<DailyStats> {

    // 根据日期查找统计数据
    DailyStats findByStatDate(@Param("statDate")LocalDate statDate);

    // 查找指定日期范围内的统计数据
    List<DailyStats> findByStatDateBetweenOrderByStatDate(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    // 获取最近N天的统计数据
    List<DailyStats> findRecentStats(@Param("days") Integer days);

    // 获取年度访问量统计报告
    List<YearTrendStatsDTO> getYearTrendStatsData();

    // 获取上周各项平均数据
    LastWeekStatsResponse findLastWeekAvgData();

    TotalStatsResponse findTotalStats(@Param("lastWeek")Boolean lastWeek);

    // 获取当月访问量的总数
    Integer getCurrentMonthVisitData();

    // 获取用户权限数量
    Integer getPerCounByUserId(Long userId);

    // 获取用户菜单数量
    Integer getMenuCountByUserId(Long userId);

    // 获取用户角色数量
    Integer getRoleCountByUserId(Long userId);

}
