package com.mysiteforme.admin.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRoleRequest implements Serializable {

    private Long id;

    private String name;

}
