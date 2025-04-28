package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = MessageConstants.User.OLD_PASSWORD_NOT_NULL)
    private String oldPwd;

    @NotBlank(message = MessageConstants.User.NEW_PASSWORD_NOT_NULL)
    private String newPwd;

    @NotBlank(message = MessageConstants.User.CONFIRM_PASSWORD_NOT_NULL)
    private String confirmPwd;
}
