package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AssignRolePermission {

    private Long roleId;

    private String loginName;

    private List<Long> permissionIds;
}
