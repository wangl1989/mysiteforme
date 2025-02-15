package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.PermissionButton;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionButtonDTO extends PermissionButton {

    private Integer page;
    private Integer limit;

}