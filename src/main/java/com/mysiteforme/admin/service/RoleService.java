/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:22
 * @ Description: 角色Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Role;
import java.util.List;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.entity.response.BaseRoleResponse;
import com.mysiteforme.admin.entity.response.PageListRoleResponse;
import com.mysiteforme.admin.entity.response.RoleMenuPerResponse;

public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     * @param request 查询角色对象
     * @return 返回分页对象集合
     */
    IPage<PageListRoleResponse> selectPageUser(PageListRoleRequest request);

    /**
     * 保存角色
     * @param request 角色对象
     */
    void saveRole(AddRoleRequest request);

    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色对象
     */
    Role getRoleById(Long id);

    /**
     * 获取当前系统中已经设置了默认角色的数量
     * @param id 角色ID
     * @return 返回已经设置了默认角色的数量
     */
    Integer getIsDefaultRoleCount(Long id);
    /**
     * 更新角色
     * @param request 角色对象
     */
    void updateRole(UpdateRoleRequest request);

    /**
     * 删除角色
     * @param id 角色Id
     */
    void deleteRole(Long id);

    /**
     * 根据角色名称获取数量
     * @param name 角色名称
     * @return 角色数量
     */
    Long getRoleNameCount(String name);

    /**
	 * 获取已经被权限ID:permissionId 分配的权角色ID集合
	 * @param permissionId 权限
	 * @return 角色ID集合
	 */
	List<Long> getRoleIdsByPermissionId(Long permissionId);

    List<BaseRoleResponse> userAllRole(Long id);

    /**
     * 获取用户角色菜单权限
     * @param id 用户ID
     * @return RoleMenuPerResponse
     */
    RoleMenuPerResponse getUserRoleMenusPermissions(Long id);

    void assignRoleMenusPermissions(SaveRoleMenuPerRequest request);

    /**
     * 获取默认角色
     * @return 角色对象
     */
    Role getDefaultRole();
}
