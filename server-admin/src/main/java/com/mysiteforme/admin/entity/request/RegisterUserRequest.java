package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = MessageConstants.User.USER_CHECK_EMAIL_CODE_EMPTY)
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;

    // 用户名不能为空
    // 验证用户名规则：英文字母开头，只能包含字母（大小写）、数字、下划线，最小长度为3，最大长度为10.
    @NotBlank(message = MessageConstants.User.LOGIN_NAME_IS_NOT_NULL)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{3,9}$", message = MessageConstants.User.INCORRECT_LOGIN_NAME_FORMAT)
    private String loginName;

    @NotBlank(message = MessageConstants.User.PASSWORD_IS_NOT_NULL)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W_]{8,20}$", message = MessageConstants.User.INCORRECT_PASSWORD_FORMAT)
    private String password;

}
