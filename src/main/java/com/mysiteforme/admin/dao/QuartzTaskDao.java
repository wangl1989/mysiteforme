package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.QuartzTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-01-24
 */
@Mapper
public interface QuartzTaskDao extends BaseMapper<QuartzTask> {

}
