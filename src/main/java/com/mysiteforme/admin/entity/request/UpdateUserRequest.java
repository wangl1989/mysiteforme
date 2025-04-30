package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateUserRequest extends BaseUserRequest{

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;

    private String loginName;

    private Set<BaseRoleRequest> roleSet;

}
