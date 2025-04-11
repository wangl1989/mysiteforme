package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.util.Set;

@Data
public class RoleMenuPerResponse {

    /**
     * 已经分配给角色的菜单ID集合
     */
    private Set<Long> menuIds;

    /**
     * 已经分配给角色的权限ID集合
     */
    private Set<Long> permissionIds;
}
