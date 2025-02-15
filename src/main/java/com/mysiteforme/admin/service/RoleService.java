/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:22
 * @ Description: 角色Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService extends IService<Role> {

    /**
     * 保存角色
     * @param role 角色对象
     */
    void saveRole(Role role);

    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色对象
     */
    Role getRoleById(Long id);

    /**
     * 更新角色
     * @param role 角色对象
     */
    void updateRole(Role role);

    /**
     * 删除角色
     * @param role 角色对象
     */
    void deleteRole(Role role);

    /**
     * 保存角色菜单关系
     * @param id 角色ID
     * @param menuSet 菜单集合
     */
    void saveRoleMenus(Long id, Set<Menu> menuSet);

    /**
     * 删除角色菜单关系
     * @param id 角色ID
     */
    void dropRoleMenus(Long id);

    /**
     * 根据角色名称获取数量
     * @param name 角色名称
     * @return 角色数量
     */
    Long getRoleNameCount(String name);

    /**
     * 获取所有角色列表
     * @return 角色列表
     */
    List<Role> selectAll();
}
