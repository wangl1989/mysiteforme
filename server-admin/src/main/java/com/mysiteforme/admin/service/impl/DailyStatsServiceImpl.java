package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.entity.*;
import com.mysiteforme.admin.entity.DTO.YearTrendStatsDTO;
import com.mysiteforme.admin.entity.response.*;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisStatsUtil;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.dao.DailyStatsDao;
import com.mysiteforme.admin.service.DailyStatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 统计汇总 服务实现类
 * </p>
 *
 * @author 昵称
 * @since 2025-05-05
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DailyStatsServiceImpl extends ServiceImpl<DailyStatsDao, DailyStats> implements DailyStatsService {

    private final RedisStatsUtil redisStatsUtil;

    private final RedisUtils redisUtils;

    private final UserService userService;

    private final MenuService menuService;

    private final PermissionDao permissionDao;
    private final RoleService roleService;

    @Override
    public TodayStatsResponse getTodayStats() {
        TodayStatsResponse response = new TodayStatsResponse();
        // 设置今天的访问量
        response.setTotalVisits(redisStatsUtil.getTodayVisits());
        // 设置今日独立访客数
        response.setUniqueVisitors(redisStatsUtil.getTodayUniqueVisitors());
        // 设置今日新用户数
        response.setNewUsers(redisStatsUtil.getTodayNewUsers());
        // 设置今日点击量
        response.setTotalClicks(redisStatsUtil.getTodayClicks());
        // 设置今日平均访问时长
        response.setAvgVisitDuration(redisStatsUtil.getTodayAvgDuration());

        // 生成总共数据
        redisStatsUtil.createTotalData(baseMapper);
        response.setAllTotalVisits(redisStatsUtil.getTotalVisits());
        response.setAllTotalClicks(redisStatsUtil.getTotalClicks());

        // 获取上周各项总数据
        TotalStatsResponse totalStatsResponse = redisStatsUtil.getLastWeekTotalData(baseMapper);
        // 设置访问总数较上周增量百分比
        // 访问总数
        Integer thisTotalCount = response.getAllTotalVisits();
        // 上周访问总数
        Integer lastTotalCount = totalStatsResponse.getTotalTotalVisits();
        if(lastTotalCount != null && lastTotalCount != 0){
            if(thisTotalCount == null || thisTotalCount  == 0){
                response.setVisitsPercent(0);
            }else {
                int result = thisTotalCount - lastTotalCount;
                // 访问总量增量比
                if(result > 0){
                    response.setVisitsPercent((result * 100) / lastTotalCount);
                } else {
                    response.setVisitsPercent(0);
                }
            }
        }else{
            response.setVisitsPercent(100);
        }

        // 获取上周各项平均数据
        LastWeekStatsResponse lastWeekAvgData =  redisStatsUtil.getLastWeekStats(baseMapper);
        // 获取上周平均每天独立访客数量
        Integer lastWeekUniqueVisitors = lastWeekAvgData.getAvgUniqueVisitors();
        // 获取今天的独立访客数量
        Integer todayUniqueVisitors = response.getUniqueVisitors();
        if(lastWeekUniqueVisitors != null && lastWeekUniqueVisitors != 0){
            if(todayUniqueVisitors == null || todayUniqueVisitors == 0){
                response.setVisitorsPercent(0);
            }else{
                // 独立访客增加的百分比
                int result = todayUniqueVisitors - lastWeekUniqueVisitors;
                if(result < 0) {
                    response.setVisitorsPercent(-((result * 100) / lastWeekUniqueVisitors));
                }else if(result == 0){
                    response.setVisitorsPercent(0);
                } else {
                    response.setVisitorsPercent(((result * 100) / lastWeekUniqueVisitors));
                }
            }
        }else{
            response.setVisitorsPercent(100);
        }
        // 获取上周平均每天的点击量
        Integer lastWeekClick = lastWeekAvgData.getAvgTotalClicks();
        // 获取今天的点击量
        Integer todayClick = response.getTotalClicks();
        if(lastWeekClick != null && lastWeekClick != 0){
            if(todayClick == null || todayClick == 0){
                response.setClickPercent(0);
            }else{
                int result = todayClick - lastWeekClick;
                if(result < 0){
                    response.setClickPercent(-((result * 100)/lastWeekClick));
                }else if (result == 0){
                    response.setClickPercent(0);
                }else{
                    response.setClickPercent(((result * 100)/lastWeekClick));
                }
            }
        }else{
            response.setClickPercent(100);
        }
        // 获取上周平均每天的新用户数量
        Integer lastWeekNewUser = lastWeekAvgData.getAvgNewUsers();
        // 获取今天的新用户数量
        Integer todayNewUser = response.getNewUsers();
        if(lastWeekNewUser != null && lastWeekNewUser != 0){
            if(todayNewUser == null || todayNewUser == 0){
                response.setNewUserPercent(0);
            }else{
                int result = todayNewUser - lastWeekNewUser;
                if(result < 0){
                    response.setNewUserPercent(-((result * 100)/lastWeekNewUser));
                } else if (result == 0) {
                    response.setNewUserPercent(0);
                } else {
                    response.setNewUserPercent(((result * 100)/lastWeekNewUser));
                }
            }
        }else{
            if(todayNewUser > 0) {
                response.setNewUserPercent(100);
            }else{
                response.setNewUserPercent(0);
            }
        }
        // 设置总用户数量
        response.setAllTotalUser(redisStatsUtil.getTotalUserCount());

        return response;
    }

    @Override
    public YearTrendStatsDataResponse getYearTrendStatsDataResponse() {
        String todayMonthKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if(redisUtils.hasKey(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY)){
            YearTrendStatsDataResponse response = redisUtils.get(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY,YearTrendStatsDataResponse.class);
            if(response != null) {
                int currentMonth = LocalDate.now().getMonthValue();
                List<Integer> monthData =  response.getVisits();
                if(!CollectionUtils.isEmpty(monthData)) {
                    Integer redisData = redisUtils.get(RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey, Integer.class);
                    // 设置准备返回的集合里的对应月份的值
                    monthData.set(currentMonth - 1,redisData);
                    response.setVisits(monthData);
                    // 更新这个年度数据缓存
                    redisUtils.set(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY,response);
                    redisUtils.expire(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY,2, TimeUnit.DAYS);
                }

            }
            return response;
        }
        // 获取数据库中的年度趋势数据
        List<YearTrendStatsDTO> trendStatsList = baseMapper.getYearTrendStatsData();

        // 创建响应对象
        YearTrendStatsDataResponse response = new YearTrendStatsDataResponse();
        // 创建月份标签和访问数据列表
        List<String> months = new ArrayList<>();
        List<Integer> visits = new ArrayList<>();

        // 创建月份映射，用于快速查找
        Map<Integer, Integer> monthVisitsMap = new HashMap<>();

        // 将查询结果转换为映射
        if (trendStatsList != null && !trendStatsList.isEmpty()) {
            for (YearTrendStatsDTO dto : trendStatsList) {
                monthVisitsMap.put(dto.getStatDate().getMonth().getValue(), dto.getMonthlyTotalVisits());
            }
        }

        // 生成所有月份（1-12月）
        for (int i = 1; i <= 12; i++) {
            String monthStr = i < 10 ? "0" + i : String.valueOf(i);
            months.add(monthStr+"月");

            // 如果该月有数据则使用实际值，否则使用0
            Integer visitCount = monthVisitsMap.getOrDefault(i, 0);
            visits.add(visitCount);
        }
        // 当前月份
        int currentMonth = LocalDate.now().getMonthValue();
        // 获取当前月份的数据
        Integer currentMonthData = visits.get(currentMonth - 1);
        int resultData = 0;
        // 如果已经有Redis对应的缓存月份的数据
        if(redisUtils.hasKey(RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey)){
            Integer redisData = redisUtils.get(RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey, Integer.class);
            resultData = currentMonthData + redisData;
        }
        // 更新缓存里的值
        redisUtils.incr(RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey, currentMonthData);
        redisUtils.expire(RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey,2, TimeUnit.DAYS);
        // 设置ANALYTICS_MONTH_TREND_DATA_KEY缓存里的list里的当前月份对应的值
        visits.set(currentMonth - 1,resultData);
        // 设置响应数据
        response.setMonths(months);
        response.setVisits(visits);
        redisUtils.set(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY,response);
        redisUtils.expire(RedisConstants.ANALYTICS_MONTH_TREND_DATA_KEY,2, TimeUnit.DAYS);
        return response;
    }

    @Override
    public TrendStatsDataResponse getTrendData(int days) {
        String todayKey = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        if(redisUtils.hasKey(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey)){
            TrendStatsDataResponse response = redisUtils.get(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey,TrendStatsDataResponse.class);
            if(response != null) {
                List<Integer> dataList = response.getVisits();
                if(!dataList.isEmpty()){
                    int lastIndex = dataList.size() - 1;
                    dataList.set(lastIndex, redisStatsUtil.getTodayVisits());
                }else{
                    dataList.add(redisStatsUtil.getTodayVisits());
                }
                response.setVisits(dataList);
                redisUtils.set(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey,response);
                redisUtils.expire(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey,2,TimeUnit.DAYS);
            }
            return response;
        }
        TrendStatsDataResponse response = new TrendStatsDataResponse();

        // 获取最近N天的数据
        List<DailyStats> statsList = baseMapper.findRecentStats(days);

        // 创建日期到访问量的映射
        Map<LocalDate, Integer> statsMap = statsList.stream()
                .collect(Collectors.toMap(
                        DailyStats::getStatDate,
                        DailyStats::getTotalVisits
                ));

        // 生成最近days天的日期列表
        LocalDate today = LocalDate.now();
        List<LocalDate> dateRange = IntStream.rangeClosed(0, days - 1)
                .mapToObj(i -> today.minusDays(days - 1 - i))
                .toList();

        // 生成日期标签（日）
        List<Integer> dateLabels = dateRange.stream()
                .map(LocalDate::getDayOfMonth)
                .collect(Collectors.toList());

        // 生成访问数据
        List<Integer> visitsData = dateRange.stream()
                .map(date -> {
                    if (date.equals(today)) {
                        return redisStatsUtil.getTodayVisits();
                    } else {
                        return statsMap.getOrDefault(date, 0);
                    }
                })
                .collect(Collectors.toList());

        // 组装结果
        response.setDays(dateLabels);
        response.setVisits(visitsData);

        // 缓存结果
        redisUtils.set(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey, response);
        redisUtils.expire(RedisConstants.ANALYTICS_DAY_TREND_DATA_KEY+todayKey, 2, TimeUnit.DAYS);

        return response;
    }

    @Override
    public void aggregateDailyStats() {
        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 检查今天的统计是否已存在
        DailyStats existingStats = baseMapper.findByStatDate(today);

        if (existingStats == null) {
            existingStats = new DailyStats();
            existingStats.setStatDate(today);
        }

        // 从Redis获取今日统计数据
        existingStats.setTotalVisits(redisStatsUtil.getTodayVisits());
        existingStats.setUniqueVisitors(redisStatsUtil.getTodayUniqueVisitors());
        existingStats.setNewUsers(redisStatsUtil.getTodayNewUsers());
        existingStats.setTotalClicks(redisStatsUtil.getTodayClicks());
        existingStats.setAvgVisitDuration(redisStatsUtil.getTodayAvgDuration());
        // 保存到数据库
        save(existingStats);
    }


    @Override
    @Cacheable(value = RedisConstants.ANALYTICS_USER_SYSTEM_DATA_KEY, key = "'id:'+#userId", unless = "#userId == null or #userId == 0")
    public IndexSystemDataResponse getUserSystemData(Long userId){
        IndexSystemDataResponse response = new IndexSystemDataResponse();
        LambdaQueryWrapper<Menu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.eq(Menu::getDelFlag,false);
        long allTotalMenuCount = menuService.count(menuLambdaQueryWrapper);
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getDelFlag,false);
        long allTotalRoleCount = roleService.count(roleLambdaQueryWrapper);
        LambdaQueryWrapper<Permission> perLambdaQueryWrapper = new LambdaQueryWrapper<>();
        perLambdaQueryWrapper.eq(Permission::getDelFlag,false);
        Long allPerTotalCount = permissionDao.selectCount(perLambdaQueryWrapper);
        int userRoleCount = baseMapper.getRoleCountByUserId(userId);
        int userPerCount = baseMapper.getPerCounByUserId(userId);
        int userTotalMenuCount = baseMapper.getMenuCountByUserId(userId);
        if(userId == 1L) {
            userRoleCount = (int)allTotalRoleCount;
            userPerCount = allPerTotalCount.intValue();
            userTotalMenuCount = (int) allTotalMenuCount;
        }
        response.setRoleCount(userRoleCount);
        response.setPermissionCount(userPerCount);
        response.setMenuCount(userTotalMenuCount);
        response.setRolePercent((userRoleCount*100)/ (int) allTotalRoleCount);
        response.setMenuPercent((userTotalMenuCount*100)/ (int) allTotalMenuCount);
        response.setPermissionPercent((userPerCount*100)/allPerTotalCount.intValue());
        response.setId(userId);
        return response;
    }

}
