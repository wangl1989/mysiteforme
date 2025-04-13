/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:42:51
 * @ Description: 权限数据层接口 提供权限的增删改查功能
 */

package com.mysiteforme.admin.dao;

import java.util.List;
import java.util.Set;

import com.mysiteforme.admin.entity.request.AssignUserPermissionRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.DTO.AssignRolePermission;
import com.mysiteforme.admin.entity.DTO.AssignUserPermission;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.entity.VO.PermissionApiVO;
import com.mysiteforme.admin.entity.VO.PermissionVO;


@Mapper
public interface PermissionDao extends BaseMapper<Permission> {

    PermissionApiVO selectApiByUrl(@Param("apiUrl") String apiUrl, @Param("method") String method);

    /**
     * 根据用户id获取对应的单独分配的权限ID集合
     * @param userId 用户ID
     * @return 权限ID集合
     */
    List<Long> getAssinUserPermission(Long userId);
    /**
     * 根据用户ID移除用户单独权限
     * @param userId 用户ID
     */
    void removeUserPermissionByUserId(Long userId);

    /**
     * 保存用户单独分配的权限
     * @param assignUserPermission 分配的权限的对象
     */
    void saveUserPermission(@Param("ass") AssignUserPermissionRequest assignUserPermission);

    /**
     * 获取所有权限
     * @return 权限集合
     */
    Set<PermissionVO> allPermission();

    /**
     * 获取权限最大排序值
     * @param menuId 菜单ID
     * @param menuType 菜单类型
     * @return 返回最大排序值
     */
    Integer permissionMaxSort(@Param("menuId") Long menuId,@Param("menuType") Integer menuType);
}
