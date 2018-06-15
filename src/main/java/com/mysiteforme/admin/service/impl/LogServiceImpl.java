package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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

    @Override
    public List<Integer> selectSelfMonthData() {
        List<Map> list =  baseMapper.selectSelfMonthData();
        //补全数据库中不存在的日期，订单数为0
        List<String> dayList = Lists.newArrayList();
        for (int i=-14;i<=0;i++){
            DateTime  dateTime = DateUtil.offsetDay(new Date(),i);
            dayList.add(dateTime.toString("yyyy-MM-dd"));
        }
        List<Integer> pv = Lists.newArrayList();
        for (int i=0;i<dayList.size();i++){
            Integer total = 0;
            for(Map map : list){
                String date  = (String)map.get("days");
                total = Integer.valueOf(map.get("total").toString());
                if(date.equalsIgnoreCase(dayList.get(i))){
                    break;
                }else{
                    total = 0;
                }
            }
            pv.add(total);
        }
        return pv;
    }
}
