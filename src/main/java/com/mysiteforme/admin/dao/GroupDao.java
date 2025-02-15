/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:43:17
 * @ Description: 分组数据层接口 提供组的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Group;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupDao extends BaseMapper<Group> {

}