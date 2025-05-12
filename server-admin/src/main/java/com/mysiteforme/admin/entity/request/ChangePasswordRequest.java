package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = MessageConstants.User.OLD_PASSWORD_NOT_NULL)
    private String oldPwd;

    @NotBlank(message = MessageConstants.User.NEW_PASSWORD_NOT_NULL)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W_]{8,20}$", message = MessageConstants.User.INCORRECT_PASSWORD_FORMAT)
    private String newPwd;

    private Long userId;
}
