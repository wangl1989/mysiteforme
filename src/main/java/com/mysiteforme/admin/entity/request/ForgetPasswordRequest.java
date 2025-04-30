package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgetPasswordRequest {

    /**
     * 邮箱
     */
    @NotBlank(message = MessageConstants.User.USER_SEND_EMAIL_EMPTY)
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;
}
