package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserRequest extends BaseUserRequest{

    // 用户名不能为空
    // 验证用户名规则：英文字母开头，只能包含字母（大小写）、数字、下划线，最小长度为3，最大长度为10.
    @NotBlank(message = MessageConstants.User.LOGIN_NAME_IS_NOT_NULL)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{3,9}$", message = MessageConstants.User.INCORRECT_LOGIN_NAME_FORMAT)
    private String loginName;

    private String password;

    private Set<BaseRoleRequest> roles;

}
