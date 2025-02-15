/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:42:26
 * @ Description: 权限API数据层接口 提供权限API的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.mysiteforme.admin.entity.PermissionApi;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


@Mapper
public interface PermissionApiDao extends BaseMapper<PermissionApi> {

}
