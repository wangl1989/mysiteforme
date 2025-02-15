package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionApi;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionApiDTO extends PermissionApi {

    private Integer page;
    private Integer limit;

}