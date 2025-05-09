package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PageListUserResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    private String loginName;

    private String nickName;

    private String icon;

    private String email;

    private String tel;

    private Boolean locked;

    private String location;

    private Boolean delFlag;

    private String remarks;

    private Date createDate;

    private String createUserNickName;

    private String createUserLoginName;

    private Long createId;

    private Date updateDate;

    private String updateUserNickName;

    private String updateUserLoginName;

    private Long updateId;

    /**
     * 用户状态
     */
    private Integer status;

    private List<BaseRoleResponse> roles;
}
