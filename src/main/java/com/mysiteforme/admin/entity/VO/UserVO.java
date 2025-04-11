/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 01:35:33
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:15:54
 * @ Description: 用户VO
 */

package com.mysiteforme.admin.entity.VO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserVO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 登录用户名
     */
    private String loginName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 用户头像
     */
    private String icon;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色
     */ 
    private Set<RoleVO> roles;
	
    /**
     * 用户权限
     */
	private Set<PermissionVO> permissions;

    /**
     * 用户是否有效
     */
    private Boolean delFlag;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否被锁定
     */
    private Boolean locked;


}
