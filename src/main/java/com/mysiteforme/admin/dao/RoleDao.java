/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:44:21
 * @ Description: 角色数据层接口 提供角色的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.request.PageListRoleRequest;
import com.mysiteforme.admin.entity.request.PageListUserRequest;
import com.mysiteforme.admin.entity.response.BaseRoleResponse;
import com.mysiteforme.admin.entity.response.PageListRoleResponse;
import com.mysiteforme.admin.entity.response.PageListUserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


@Mapper
public interface RoleDao extends BaseMapper<Role> {

    IPage<PageListRoleResponse> selectPageRole(IPage<PageListRoleResponse> page, @Param("request") PageListRoleRequest request);

    List<BaseRoleResponse> myRoleList(Long userId);

    Role selectRoleById(@Param("id") Long id);

    void saveRoleMenus(@Param("roleId") Long id, @Param("menus") Set<Long> menus);

    void saveRolePermissions(@Param("roleId") Long id, @Param("permissions") Set<Long> permissions);

    void dropRoleMenus(@Param("roleId")Long roleId);

    void dropRoleUsers(@Param("roleId")Long roleId);

    void dropRolePermissions(@Param("roleId")Long roleId);

    /**
     * 这个角色ID被哪些用户分配了
     * @param roleId 角色ID
     * @return 角色ID集合
     */
    Set<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 查询这个menuId被哪些角色分配了
     * @return 角色ID集合
     */
    Set<Long> getRoleIdsByMenuId(Long menuId);

    /**
     * 查询这个permissionId被哪些角色分配了
     * @return 角色ID集合
     */
    List<Long> getRoleIdsByPermissionId(Long permissionId);

    /**
     * 根据角色ID获取权限ID集合
     * @param roleId 角色ID
     * @return 权限ID集合
     */
    Set<Long> getPemissionIdsByRoleId(Long roleId);

    /**
     * 根据角色ID获取菜单ID集合
     * @param roleId 角色ID
     * @return 菜单ID集合
     */
    Set<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 查询这个permissionId被哪些用户分配了
     * @param permissionId 权限ID
     * @return PermissionForUserDTO对象集合{用户ID以及用户名}
     */
    Set<Long> getUserIdsByPermissionId(Long permissionId);
}