package com.mysiteforme.admin.dao;

import com.mysiteforme.admin.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Mapper
public interface PermissionDao extends BaseMapper<Permission> {

}
