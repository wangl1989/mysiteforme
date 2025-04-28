package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;

    private String loginName;

    private Set<BaseRoleRequest> roleSet;

    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;

    @Pattern(regexp = "^(13[0-9]|14[57]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = MessageConstants.User.INCORRECT_TEL_FORMAT)
    private String tel;

    private String nickName;

    private String icon;

    private String remarks;
}
