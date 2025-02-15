/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 01:35:33
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:15:54
 * @ Description: 用户VO
 */

package com.mysiteforme.admin.entity.VO;

import java.util.Set;

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
public class UserVO {
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


}
