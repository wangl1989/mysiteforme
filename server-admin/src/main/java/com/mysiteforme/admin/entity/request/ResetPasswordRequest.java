package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = MessageConstants.User.USER_SEND_EMAIL_EMPTY)
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;

    @NotBlank(message = MessageConstants.User.PASSWORD_IS_NOT_NULL)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W_]{8,20}$", message = MessageConstants.User.INCORRECT_PASSWORD_FORMAT)
    private String password;

}
