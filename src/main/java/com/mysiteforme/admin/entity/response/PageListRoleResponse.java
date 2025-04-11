package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PageListRoleResponse implements Serializable {

    private Long id;

    private String name;

    private String remarks;

    private Boolean delFlag;

    private Long createId;

    private Long updateId;

    private Date createDate;

    private Date updateDate;

    private String createUserNickName;

    private String updateUserNickName;

    private String createUserLoginName;

    private String updateUserLoginName;

}
