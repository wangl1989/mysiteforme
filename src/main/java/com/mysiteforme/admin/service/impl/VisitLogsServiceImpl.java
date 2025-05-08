package com.mysiteforme.admin.service.impl;

import com.mysiteforme.admin.dao.ClickEventsDao;
import com.mysiteforme.admin.entity.ClickEvents;
import com.mysiteforme.admin.entity.DTO.*;
import com.mysiteforme.admin.entity.UserDevice;
import com.mysiteforme.admin.entity.VO.DeviceTokenInfo;
import com.mysiteforme.admin.entity.request.AddClickEventsRequest;
import com.mysiteforme.admin.entity.request.PageListVisitLogsRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisStatsUtil;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.redis.TokenStorageService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserDeviceService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.ToolUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.VisitLogs;
import com.mysiteforme.admin.dao.VisitLogsDao;
import com.mysiteforme.admin.service.VisitLogsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 访问记录表 服务实现类
 * </p>
 *
 * @author wangl1989
 * @since 2025-05-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class VisitLogsServiceImpl extends ServiceImpl<VisitLogsDao, VisitLogs> implements VisitLogsService {

    private final TokenStorageService tokenStorageService;

    private final UserCacheService userCacheService;

    private final ClickEventsDao clickEventsDao;

    private final UserDeviceService userDeviceService;

    private final RedisUtils redisUtils;

    private final RedisStatsUtil redisStatsUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询访问记录表
     */
    @Override
    public IPage<VisitLogs> selectPageVisitLogs(PageListVisitLogsRequest request){
        LambdaQueryWrapper<VisitLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitLogs::getDelFlag,false);
        if(request != null){
            if(StringUtils.isNotBlank(request.getProvince())){
                wrapper.like(VisitLogs::getProvince,request.getProvince());
            }
            if(StringUtils.isNotBlank(request.getCity())){
                wrapper.like(VisitLogs::getCity,request.getCity());
            }
            if(StringUtils.isNotBlank(request.getRegion())){
                wrapper.like(VisitLogs::getRegion,request.getRegion());
            }
            if(StringUtils.isNotBlank(request.getOs())){
                wrapper.like(VisitLogs::getOs,request.getOs());
            }
            if(StringUtils.isNotBlank(request.getBrowser())){
                wrapper.like(VisitLogs::getBrowser,request.getBrowser());
            }
        } else {
            request = new PageListVisitLogsRequest();
        }
        wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), VisitLogs::getCreateDate);
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    // 判断是否为新用户的辅助方法
    private boolean isNewUser(Long userId) {
        Set<Object> preTodayUserIds = redisStatsUtil.getUsersBeforTodayCreated();
        if (preTodayUserIds == null || preTodayUserIds.isEmpty()) {
            // 如果集合为空或null，假设是新用户
            return true;
        }
        Set<Long> ids = preTodayUserIds.stream()
                .map(Object::toString)
                .filter(str -> {
                    try {
                        Long.parseLong(str);
                        return true;
                    } catch (NumberFormatException e) {
                        log.warn("无法将值 '{}' 转换为Long类型", str);
                        return false;
                    }
                })
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        return !ids.contains(userId);
    }

    /**
     * 生成访问记录日志
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createVisitLogs(VisitLogs logs) {
        // 验证设备ID
        if(StringUtils.isBlank(logs.getDeviceId())) {
            HttpServletRequest request = ToolUtil.getCurrentRequest();
            String deviceId = request.getHeader(Constants.DEVICE_ID);
            if (StringUtils.isBlank(deviceId)) {
                log.error("设备ID为空，退出日志记录");
                return;
            } else {
                logs.setDeviceId(deviceId);
            }
        }
        
        // 验证登录用户的设备
        if(StringUtils.isNotBlank(logs.getLoginName())) {
            List<DeviceTokenInfo> deviceTokenInfoList = tokenStorageService.getAllUserDevices(logs.getLoginName());
            if (deviceTokenInfoList.stream().noneMatch(d -> d.getDeviceId().equalsIgnoreCase(logs.getDeviceId()))) {
                log.error("请求中设备ID为【无效的设备ID】,退出日志");
                return;
            }
        }
        
        // 使用Redis管道或批量操作来减少网络往返次数
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            String todayKey = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String todayMonthKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // 增加访问计数
            connection.stringCommands().incr((RedisConstants.ANALYTICS_VISITS_KEY + todayKey).getBytes());
            // 增加月度统计数量
            connection.stringCommands().incr((RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey).getBytes());
            // 增加访问总计
            connection.stringCommands().incr((RedisConstants.ANALYTICS_TOTAL_VISITS_KEY).getBytes());

            // 记录独立访客
            if (logs.getDeviceId() != null && !logs.getDeviceId().isEmpty()) {
                connection.setCommands().sAdd(
                        (RedisConstants.ANALYTICS_VISITORS_KEY + todayKey).getBytes(),
                        logs.getDeviceId().getBytes()
                );
            }

            // 记录访问时长
            if (logs.getTimeOnPage() > 0) {
                int durationSeconds = logs.getTimeOnPage() / 1000;
                connection.stringCommands().incrBy(
                        (RedisConstants.ANALYTICS_DURATION_KEY + todayKey).getBytes(),
                        durationSeconds
                );
                connection.stringCommands().incr(
                        (RedisConstants.ANALYTICS_DURATION_COUNT_KEY + todayKey).getBytes()
                );
            }

            // 记录新用户
            if(logs.getUserId() != null && logs.getUserId() != 0 && isNewUser(logs.getUserId())) {
                connection.stringCommands().incr((RedisConstants.ANALYTICS_NEW_USERS_KEY + todayKey).getBytes());
                connection.stringCommands().incr((RedisConstants.ANALYTICS_TOTAL_USER_KEY).getBytes());
            }

            // 为所有统计数据设置过期时间
            int expiryDays = 2;
            connection.keyCommands().expire((RedisConstants.ANALYTICS_VISITS_KEY + todayKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_MONTH_VISITS_KEY + todayMonthKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_TOTAL_VISITS_KEY).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_VISITORS_KEY + todayKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_DURATION_KEY + todayKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_DURATION_COUNT_KEY + todayKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_NEW_USERS_KEY + todayKey).getBytes(), expiryDays*24*60*60);
            connection.keyCommands().expire((RedisConstants.ANALYTICS_TOTAL_USER_KEY).getBytes(), expiryDays*24*60*60);

            return null;
        });
        
        // 设置访问时间
        logs.setVisitTime(LocalDateTime.now());
        
        // 获取地理位置信息
        try {
            HttpServletRequest request = ToolUtil.getCurrentRequest();
            String ip = request.getRemoteAddr();
            LocationDTO location = userCacheService.getLocationByIp(ip);
            if(location != null){
                BeanUtils.copyProperties(location, logs);
                logs.setIpAddress(location.getIp());
                AgentDTO agent = ToolUtil.getOsAndBrowserInfo(request);
                BeanUtils.copyProperties(agent, logs);
            } else {
                return;
            }
        } catch (Exception e) {
            log.error("获取地理位置出现异常:{}", e.getMessage());
            return;
        }
        
        // 保存到数据库
        save(logs);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createClickEvents(AddClickEventsRequest request) {
        // 增加点击计数
        redisStatsUtil.incrementClicks();
        redisStatsUtil.incrementTotalClicks();

        HttpServletRequest r = ToolUtil.getCurrentRequest();
        List<ClickEvents> events = request.getEvents();
        String deviceId = "";
        for(ClickEvents clickEvents : events){
            clickEvents.setUserId(request.getUserId());
            clickEvents.setLoginName(request.getLoginName());
            clickEvents.setNickName(request.getNickName());
            clickEvents.setClickTime(LocalDateTime.now());
            deviceId = clickEvents.getDeviceId();
        }
        if(StringUtils.isBlank(deviceId)) {
            deviceId = r.getHeader(Constants.DEVICE_ID);
            if(StringUtils.isBlank(deviceId)){
                throw MyException.builder().businessError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
            }
            String finalDeviceId = deviceId;
            events.forEach(c -> c.setDeviceId(finalDeviceId));
        }
        clickEventsDao.insert(events);
        final String finalDeviceId = deviceId;
        if(request.getUserId() != null && request.getUserId() != 0){
            // 已登录用户的设备活跃度更新
            String cacheKey = String.format(RedisConstants.DEVICE_ACTIVITY_USER_PREFIX,request.getUserId()) + finalDeviceId;
            if(!redisUtils.hasKey(cacheKey)) {
                UserDevice userDevice = userDeviceService.getCurrentUserDevice();
                if (userDevice == null) {
                    userDevice = new UserDevice();
                    userDevice.setDeviceId(deviceId);
                    userDevice.setUserId(request.getUserId());
                    userDevice.setFirstSeen(LocalDateTime.now());
                    userDevice.setCreateId(request.getUserId());
                }
                userDevice.setUpdateId(request.getUserId());
                AgentDTO agent = ToolUtil.getOsAndBrowserInfo(r);
                userDevice.setUserAgent(agent.getUserAgent());
                // 设置活跃时间
                userDevice.setLastSeen(LocalDateTime.now());
                userDeviceService.saveOrUpdate(userDevice);
                // 设置缓存，5分钟内不再更新数据库
                redisUtils.set(cacheKey, LocalDateTime.now().toString(), 5, TimeUnit.MINUTES);
            }
        }else{
            // 未登录用户的设备活跃度更新
            String cacheKey = RedisConstants.DEVICE_ACTIVITY_ANONYMOUS_PREFIX + finalDeviceId;
            if (!redisUtils.hasKey(cacheKey)) {
                LambdaQueryWrapper<UserDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserDevice::getDeviceId, deviceId);
                lambdaQueryWrapper.isNull(UserDevice::getUserId);
                List<UserDevice> deviceList = userDeviceService.list(lambdaQueryWrapper);
                UserDevice userDevice;
                if (CollectionUtils.isEmpty(deviceList)) {
                    userDevice = new UserDevice();
                    userDevice.setDeviceId(deviceId);
                    userDevice.setFirstSeen(LocalDateTime.now());
                } else {
                    userDevice = deviceList.get(0);
                }
                AgentDTO agent = ToolUtil.getOsAndBrowserInfo(r);
                userDevice.setUserAgent(agent.getUserAgent());
                // 设置活跃时间
                userDevice.setLastSeen(LocalDateTime.now());
                userDeviceService.saveOrUpdate(userDevice);
                // 设置缓存，5分钟内不再更新数据库
                redisUtils.set(cacheKey, LocalDateTime.now().toString(), 5, TimeUnit.MINUTES);
            }

        }
    }

}
