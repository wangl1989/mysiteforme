/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:42:51
 * @ Description: 权限数据层接口 提供权限的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.mysiteforme.admin.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


@Mapper
public interface PermissionDao extends BaseMapper<Permission> {

}
