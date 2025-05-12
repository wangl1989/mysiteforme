package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BaseUserRequest {

    @Email(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = MessageConstants.User.INCORRECT_TEL_FORMAT)
    private String tel;

    private String nickName;

    private String icon;

    private String remarks;

}
