/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:41:59
 * @ Description: 系统日志数据层接口 提供系统日志的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Log;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;


public interface LogDao extends BaseMapper<Log> {

    @MapKey("")
    List<Map<String,Object>> selectSelfMonthData();
}
