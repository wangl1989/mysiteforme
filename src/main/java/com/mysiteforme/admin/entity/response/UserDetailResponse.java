package com.mysiteforme.admin.entity.response;

import com.mysiteforme.admin.entity.VO.RoleVO;
import lombok.Data;

import java.util.Set;

@Data
public class UserDetailResponse {

    private Long id;

    private String loginName;

    private String nickName;

    private String icon;

    private String email;

    private String tel;

    private String remarks;

    private Set<RoleVO> roles;

}
