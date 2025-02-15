package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionGroup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionGroupDTO extends PermissionGroup {

    private Integer page;
    private Integer limit;

}