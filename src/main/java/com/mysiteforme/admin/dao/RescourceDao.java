/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:44:07
 * @ Description: 资源数据层接口 提供资源的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Rescource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RescourceDao extends BaseMapper<Rescource> {

}
