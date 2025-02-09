package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.dao.LogDao;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.service.LogService;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统日志 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LogServiceImpl extends ServiceImpl<LogDao, Log> implements LogService {

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
}
