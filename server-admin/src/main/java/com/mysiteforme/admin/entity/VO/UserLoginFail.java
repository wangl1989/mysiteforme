/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 01:20:17
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:49:23
 * @ Description: 用户登录失败记录展示对象
 */

package com.mysiteforme.admin.entity.VO;

import lombok.*;

@Data
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
