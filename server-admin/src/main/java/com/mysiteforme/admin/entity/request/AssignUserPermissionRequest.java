package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignUserPermissionRequest {

    @NotNull(message = MessageConstants.User.ASSIGN_USER_ID_EMPTY)
    private Long userId;

    private List<Long> permissionIds;
}
