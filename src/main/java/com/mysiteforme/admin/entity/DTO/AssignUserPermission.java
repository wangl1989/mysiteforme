package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AssignUserPermission {

    private Long userId;

    private String loginName;

    private List<Long> permissionIds;
}
