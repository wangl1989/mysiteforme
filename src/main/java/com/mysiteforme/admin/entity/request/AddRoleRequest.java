package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleRequest {

    @NotBlank(message = MessageConstants.Role.ROLE_NAME_EMPTY)
    private String name;

    private String remarks;

}
