package com.mysiteforme.admin.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoginFail {

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 提交登录时间
     */
    private long loginTime;


}
