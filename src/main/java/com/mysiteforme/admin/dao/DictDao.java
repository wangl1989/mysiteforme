package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
  * 字典表 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2017-11-26
 */
@Mapper
public interface DictDao extends BaseMapper<Dict> {

}