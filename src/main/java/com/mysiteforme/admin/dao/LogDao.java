package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统日志 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-01-14
 */
@Mapper
public interface LogDao extends BaseMapper<Log> {

    List<Map<String,Object>> selectSelfMonthData();
}
