package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRoleResponse implements Serializable {

    private Long id;

    private String name;

    private Boolean isDefault;

}
