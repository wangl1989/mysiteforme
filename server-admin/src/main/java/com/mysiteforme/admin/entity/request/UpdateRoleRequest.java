package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;

    @NotBlank(message = MessageConstants.Role.ROLE_NAME_EMPTY)
    private String name;

    @NotNull(message = MessageConstants.Role.ROLE_IS_DEFAULT_EMPTY)
    private Boolean isDefault;

    private String remarks;
}
