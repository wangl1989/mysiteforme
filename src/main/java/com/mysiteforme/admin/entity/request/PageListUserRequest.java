package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListUserRequest extends BasePageRequest{

    private String loginName;

    private String tel;

    private String email;

    private Boolean sortByCreateDateAsc;

    private Boolean sortByLoginNameAsc;

    private Boolean sortByUpdateDateAsc;
}
