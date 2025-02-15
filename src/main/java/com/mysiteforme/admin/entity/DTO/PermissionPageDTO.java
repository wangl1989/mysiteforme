package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionPageDTO extends PermissionPage {

    private Integer page;
    private Integer limit;

}