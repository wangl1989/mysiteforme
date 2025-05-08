package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnalyticsUserResponse implements Serializable {

    private Long id;

    /**
     * 用户头像
     */
    private String icon;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户登录账号
     */
    private String loginName;

    /**
     * 用户地区
     */
    private String location;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 资料完善度
     */
    private Integer percent;
}
