package com.mysiteforme.admin.entity.request;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Data
public class SaveRoleMenuPerRequest {
    /**
     * 角色ID
     */
    @NotNull
    private Long roleId;

    /**
     * 已经分配给角色的菜单ID集合
     */
    private Set<Long> menuIds;

    /**
     * 已经分配给角色的权限ID集合
     */
    private Set<Long> permissionIds;
}
