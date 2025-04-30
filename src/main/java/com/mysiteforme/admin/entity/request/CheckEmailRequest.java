package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckEmailRequest {

    @Size(min = 6,max = 6,message = MessageConstants.User.EMAIL_CODE_LENGTH_ERROR)
    @NotBlank(message = MessageConstants.User.USER_CHECK_EMAIL_CODE_EMPTY)
    private String code;

    @NotBlank(message = MessageConstants.User.USER_SEND_EMAIL_EMPTY)
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;
}
