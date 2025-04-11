package com.mysiteforme.admin.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionForUserDTO {

    private Long userId;

    private String userName;

}
