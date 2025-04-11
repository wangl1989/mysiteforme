package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class AddUserRequest {

    // 用户名不能为空
    // 验证用户名规则：英文字母开头，只能包含字母（大小写）、数字、下划线，最小长度为3，最大长度为10.
    @NotBlank(message = MessageConstants.User.LOGIN_NAME_IS_NOT_NULL)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{3,9}$", message = MessageConstants.User.INCORRECT_LOGIN_NAME_FORMAT)
    private String loginName;

    private Set<Role> roles;

    private String password;

    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = MessageConstants.User.INCORRECT_EMAIL_FORMAT)
    private String email;

    @Pattern(regexp = "^(13[0-9]|14[57]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = MessageConstants.User.INCORRECT_TEL_FORMAT)
    private String tel;

    private String nickName;

    private String icon;

    private String remarks;
}
