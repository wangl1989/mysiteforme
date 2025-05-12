package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdatePermissionRequest extends BasePermissionRequest{

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;

}
