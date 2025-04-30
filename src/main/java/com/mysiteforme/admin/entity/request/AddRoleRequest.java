package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRoleRequest {

    @NotBlank(message = MessageConstants.Role.ROLE_NAME_EMPTY)
    private String name;

    /**
     * 是否为默认角色
     */
    @NotNull(message = MessageConstants.Role.ROLE_IS_DEFAULT_EMPTY)
    private Boolean isDefault;

    private String remarks;

}
