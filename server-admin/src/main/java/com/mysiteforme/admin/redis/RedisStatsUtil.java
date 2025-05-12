package com.mysiteforme.admin.redis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import com.mysiteforme.admin.dao.DailyStatsDao;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.response.LastWeekStatsResponse;
import com.mysiteforme.admin.entity.response.TotalStatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *     统计用户行为数据使用Redis工具
 * </p>
 */

@Slf4j
@Component
public class RedisStatsUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserDao userDao;

    public RedisStatsUtil(RedisTemplate<String, Object> redisTemplate, UserDao userDao) {
        this.redisTemplate = redisTemplate;
        this.userDao = userDao;
    }


    // 获取今天的日期键
    private String getTodayKey() {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }

    public Integer getTotalUserCount(){
        if(redisTemplate.hasKey(RedisConstants.ANALYTICS_TOTAL_USER_KEY)){
            return (Integer)redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_TOTAL_USER_KEY);
        }
        Long count = userDao.selectCount(new LambdaQueryWrapper<>());
        int result;
        if(count != null){
            result =  count.intValue();
        }else{
            result = 0;
        }
        if(result > 0) {
            redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_USER_KEY, result);
        } else {
            redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_USER_KEY);
        }
        redisTemplate.expire(RedisConstants.ANALYTICS_TOTAL_USER_KEY,2,TimeUnit.DAYS);
        return result;
    }

    /**
     * 获取截止上周各项数据总数
     */
    public TotalStatsResponse getLastWeekTotalData(DailyStatsDao dao){
        if(redisTemplate.hasKey(RedisConstants.ANALYTICS_LAST_WEEK_TOTAL_KEY)){
            return (TotalStatsResponse)redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_LAST_WEEK_TOTAL_KEY);
        }
        TotalStatsResponse response = dao.findTotalStats(true);
        if(response != null) {
            redisTemplate.opsForValue().set(RedisConstants.ANALYTICS_LAST_WEEK_TOTAL_KEY, response);
            redisTemplate.expire(RedisConstants.ANALYTICS_LAST_WEEK_TOTAL_KEY, 2, TimeUnit.DAYS);
        }
        return response;
    }

    /**
     * 获取总共数据
     */
    public void createTotalData(DailyStatsDao dao){
        if(redisTemplate.hasKey(RedisConstants.ANALYTICS_TOTAL_DATA_KEY + getTodayKey())){
            return;
        }
        TotalStatsResponse response = dao.findTotalStats(false);
        if(response != null) {
            // 设置当天的SQL查询缓存
            redisTemplate.opsForValue().set(RedisConstants.ANALYTICS_TOTAL_DATA_KEY + getTodayKey(), response);
            redisTemplate.expire(RedisConstants.ANALYTICS_TOTAL_DATA_KEY + getTodayKey(), 2, TimeUnit.DAYS);
            // 设置总访问缓存
            if (response.getTotalTotalVisits() != null && response.getTotalTotalVisits() != 0) {
                redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_VISITS_KEY, response.getTotalTotalVisits());
            } else {
                redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_VISITS_KEY);
            }
            redisTemplate.expire(RedisConstants.ANALYTICS_TOTAL_VISITS_KEY, 2, TimeUnit.DAYS);
            // 设置总点击缓存
            if (response.getTotalClicks() != null && response.getTotalClicks() != 0) {
                redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY, response.getTotalClicks());
            } else {
                redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY);
            }
            redisTemplate.expire(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY,2,TimeUnit.DAYS);
        }
    }

    /**
     * 获取上周各项数据
     */
    public LastWeekStatsResponse getLastWeekStats(DailyStatsDao dao){
        if(redisTemplate.hasKey(RedisConstants.ANALYTICS_LAST_WEEK_DATA_KEY)){
            return (LastWeekStatsResponse)redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_LAST_WEEK_DATA_KEY);
        }
        LastWeekStatsResponse response = dao.findLastWeekAvgData();
        if(response != null) {
            redisTemplate.opsForValue().set(RedisConstants.ANALYTICS_LAST_WEEK_DATA_KEY, response);
            redisTemplate.expire(RedisConstants.ANALYTICS_LAST_WEEK_DATA_KEY, 2, TimeUnit.DAYS);
        }
        return response;
    }

    // 获取今天之前创建的用户ID集合
    public Set<Object> getUsersBeforTodayCreated(){
        if(redisTemplate.hasKey(RedisConstants.USER_BEFOR_TODAY_CREATED + getTodayKey())){
            return redisTemplate.opsForSet().members(RedisConstants.USER_BEFOR_TODAY_CREATED + getTodayKey());
        }
        // 获取今天凌晨的时间点
        LocalDate today = LocalDate.now();
        Date todayStart = java.sql.Date.valueOf(today);
        // 查询创建日期小于今天的用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.lt(User::getCreateDate, todayStart);
        List<User> userList = userDao.selectList(lambdaQueryWrapper);

        // 提取用户ID并存入Redis
        Set<Object> userIds = userList.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        // 如果集合不为空，则存入Redis
        if (!userIds.isEmpty()) {
            String redisKey = RedisConstants.USER_BEFOR_TODAY_CREATED + getTodayKey();
            // 将Set<Long>转换为Long[]数组
            redisTemplate.opsForSet().add(redisKey, userIds.toArray());
            // 设置过期时间为2天，避免长期占用内存
            redisTemplate.expire(redisKey, 2, TimeUnit.DAYS);
            return userIds;
        }
        return Sets.newHashSet();
    }

    // 获取总访问量
    public Integer getTotalVisits() {
        Object value = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_TOTAL_VISITS_KEY);
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 获取总点击量
    public Integer getTotalClicks() {
        Object value = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY);
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 增加点击计数
    public void incrementClicks() {
        redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_CLICKS_KEY + getTodayKey());
        redisTemplate.expire(RedisConstants.ANALYTICS_CLICKS_KEY + getTodayKey(),2,TimeUnit.DAYS);
    }

    // 增加点击计数
    public void incrementTotalClicks() {
        redisTemplate.opsForValue().increment(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY + getTodayKey());
        redisTemplate.expire(RedisConstants.ANALYTICS_TOTAL_CLICK_KEY + getTodayKey(),2,TimeUnit.DAYS);

    }

    // 获取今日访问量
    public Integer getTodayVisits() {
        Object value = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_VISITS_KEY + getTodayKey());
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 获取今日独立访客数
    public Integer getTodayUniqueVisitors() {
        Long size = redisTemplate.opsForSet().size(RedisConstants.ANALYTICS_VISITORS_KEY + getTodayKey());
        return size != null ? size.intValue() : 0;
    }

    // 获取今日新用户数
    public Integer getTodayNewUsers() {
        Object value = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_NEW_USERS_KEY + getTodayKey());
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 获取今日点击量
    public Integer getTodayClicks() {
        Object value = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_CLICKS_KEY + getTodayKey());
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 获取今日平均访问时长
    public Integer getTodayAvgDuration() {
        Object totalDuration = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_DURATION_KEY + getTodayKey());
        Object count = redisTemplate.opsForValue().get(RedisConstants.ANALYTICS_DURATION_COUNT_KEY + getTodayKey());

        if (totalDuration != null && count != null) {
            long total = Long.parseLong(totalDuration.toString());
            long visits = Long.parseLong(count.toString());
            return visits > 0 ? (int)(total / visits) : 0;
        }
        return 0;
    }
}
