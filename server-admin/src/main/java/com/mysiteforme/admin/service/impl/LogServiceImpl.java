/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:00
 * @ Description: 日志服务实现类 提供日志的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.date.BetweenFormatter;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.DTO.IndexLogDTO;
import com.mysiteforme.admin.entity.request.PageListSystemLogRequest;
import com.mysiteforme.admin.entity.response.IndexLogResponse;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.dao.LogDao;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.service.LogService;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.springframework.util.CollectionUtils;


@Service
@Transactional(rollbackFor = Exception.class)
public class LogServiceImpl extends ServiceImpl<LogDao, Log> implements LogService {

    private final RedisUtils redisUtils;

    private final PermissionService permissionService;

    public LogServiceImpl(RedisUtils redisUtils, PermissionService permissionService) {
        this.redisUtils = redisUtils;
        this.permissionService = permissionService;
    }

    /**
     * 查询最近15天的日志数量统计
     * 如果某天没有日志记录，则补充为0
     * @return 最近15天的每日日志数量列表
     */
    @Override
    public List<Integer> selectSelfMonthData() {
        // 获取数据库中的日志统计数据
        List<Map<String,Object>> list =  baseMapper.selectSelfMonthData();
        
        // 生成最近15天的日期列表（从今天往前推14天）
        List<String> dayList = Lists.newArrayList();
        for (int i=-14;i<=0;i++){
            DateTime  dateTime = DateUtil.offsetDay(new Date(),i);
            dayList.add(dateTime.toString("yyyy-MM-dd"));
        }
        
        // 将数据库统计结果转换为每日数量列表，对缺失的日期补0
        List<Integer> pv = Lists.newArrayList();
        for (String s : dayList) {
            int total = 0;
            for (Map<String,Object> map : list) {
                String date = (String) map.get("days");
                total = Integer.parseInt(map.get("total").toString());
                if (date.equalsIgnoreCase(s)) {
                    break;
                } else {
                    total = 0;
                }
            }
            pv.add(total);
        }
        return pv;
    }

    @Override
    public IPage<Log> selectPageLogs(PageListSystemLogRequest request) {
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        if(request != null) {
            if (StringUtils.isNotBlank(request.getType())) {
                wrapper.eq(Log::getType, request.getType());
            }
            if (StringUtils.isNotBlank(request.getTitle())) {
                wrapper.like(Log::getTitle, request.getTitle());
            }
            if (StringUtils.isNotBlank(request.getUsername())) {
                wrapper.eq(Log::getUsername, request.getUsername());
            }
            if (StringUtils.isNotBlank(request.getHttpMethod())) {
                wrapper.eq(Log::getHttpMethod, request.getHttpMethod());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(),Log::getCreateDate);
        }else{
            request = new PageListSystemLogRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    @Override
    public List<IndexLogResponse> getIndexLogList(Integer limit){
        // 尝试从缓存获取
        Long size = redisUtils.getListSize(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY);
        if(size != null && size > 0){
            int endIndex = Math.min(limit - 1, size.intValue() - 1);
            List<Object> list = redisUtils.lRange(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY, 0, endIndex);
            if(!CollectionUtils.isEmpty(list)){
                return list.stream().map(obj -> {
                    if(obj instanceof IndexLogDTO dto){
                        IndexLogResponse response = new IndexLogResponse();
                        BeanUtils.copyProperties(obj,response);
                        // 设置图标
                        String icon = permissionService.getApiIconInfo(dto.getRequestUri(),dto.getMethod());
                        if(StringUtils.isNotBlank(icon)) {
                            response.setIcon(icon);
                        }
                        response.setCreateTime(DateUtil.formatBetween(dto.getCreateDate(),new Date(),BetweenFormatter.Level.SECOND));
                        return response;
                    }else if (obj instanceof Map<?, ?> map) {
                        // 如果是Map类型（JSON反序列化的结果可能是LinkedHashMap）
                        IndexLogResponse response = new IndexLogResponse();
                        // 从Map中提取字段
                        if (map.containsKey("id")) {
                            response.setId(Long.valueOf(map.get("id").toString()));
                        }
                        if (map.containsKey("userName")) {
                            response.setUserName((String) map.get("userName"));
                        }
                        if (map.containsKey("createTime")) {
                            Date date = (Date) map.get("createTime");
                            response.setCreateTime(DateUtil.formatBetween(date,new Date(),BetweenFormatter.Level.SECOND));
                        }
                        if (map.containsKey("title")){
                            response.setTitle((String) map.get("title"));
                        }
                        if (map.containsKey("method")){
                            response.setMethod((String) map.get("method"));
                        }
                        if (map.containsKey("httpMethod") && map.containsKey("requestUri")){
                            String icon = permissionService.getApiIconInfo((String)map.get("requestUri"),(String)map.get("httpMethod"));
                            if(StringUtils.isNotBlank(icon)) {
                                response.setIcon(icon);
                            }
                        }
                        return response;
                    } else {
                        // 如果是其他类型，尝试使用JSON转换
                        try {
                            String json = JSON.toJSONString(obj);
                            return JSON.parseObject(json, IndexLogResponse.class);
                        } catch (Exception e) {
                            log.error("转换缓存对象到IndexLogResponse失败", e);
                            return null;
                        }
                    }
                })
                .filter(Objects::nonNull) // 过滤掉转换失败的null值
                .collect(Collectors.toList());
            }
        } else {
            LambdaQueryWrapper<Log> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.orderByDesc(Log::getCreateDate);
            Page<Log> logPage = this.page(new Page<>(1, limit>RedisConstants.ANALYTICS_INDEX_LOG_SIZE?RedisConstants.ANALYTICS_INDEX_LOG_SIZE:limit), lambdaQueryWrapper);
            List<Log> logList = logPage.getRecords();
            if (!CollectionUtils.isEmpty(logList)) {
                List<IndexLogResponse> indexList = new ArrayList<>();
                List<IndexLogDTO> dtoList = new ArrayList<>();
                logList.forEach(thisLog -> {
                    IndexLogDTO dto = new IndexLogDTO();
                    dto.setId(thisLog.getId());
                    dto.setUserName(thisLog.getUsername());
                    dto.setTitle(thisLog.getTitle());
                    dto.setCreateDate(thisLog.getCreateDate());
                    dto.setMethod(thisLog.getHttpMethod());
                    dto.setHttpMethod(thisLog.getHttpMethod());
                    dto.setRequestUri(thisLog.getRequestUri());
                    dtoList.add(dto);
                    IndexLogResponse response = new IndexLogResponse();
                    BeanUtils.copyProperties(dto,response);
                    // 设置图标
                    String icon = permissionService.getApiIconInfo(dto.getRequestUri(),dto.getMethod());
                    response.setIcon(icon);
                    response.setCreateTime(DateUtil.formatBetween(dto.getCreateDate(),new Date(),BetweenFormatter.Level.SECOND));
                    indexList.add(response);
                });
                redisUtils.rightPushListAll(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY,dtoList.toArray());
                redisUtils.expire(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY,20, TimeUnit.MINUTES);
                return indexList;
            }
        }
        return new ArrayList<>();
    }
}
