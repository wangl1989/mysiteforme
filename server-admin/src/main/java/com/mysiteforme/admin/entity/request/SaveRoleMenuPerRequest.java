package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor  // 添加无参构造函数
public class SaveRoleMenuPerRequest {
    /**
     * 角色ID
     */
    @NotNull(message = MessageConstants.Role.ROLE_ID_CAN_NOT_NULL)
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
