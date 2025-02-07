package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 任务执行日志 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-01-25
 */
@Mapper
public interface QuartzTaskLogDao extends BaseMapper<QuartzTaskLog> {

}
