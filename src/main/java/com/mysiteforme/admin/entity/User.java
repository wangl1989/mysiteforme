/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:54:05
 * @ Description: 用户基类
 */

package com.mysiteforme.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Sets;
import com.mysiteforme.admin.base.DataEntity;
import com.mysiteforme.admin.entity.VO.PermissionVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Data
public class User extends DataEntity {

    /**
     * 登录名
     */
	@TableField("login_name")
	private String loginName;
    /**
     * 昵称
     */
	@TableField(value = "nick_name")
	private String nickName;
    /**
     * 密码
     */
	private String password;
    /**
     * shiro加密盐
     */
	private String salt;
    /**
     * 手机号码
     */
	private String tel;
    /**
     * 邮箱地址
     */
	private String email;
	
	/**
	 * 账户是否锁定
	 */
	private Boolean locked;

	private String icon;

	@TableField(exist=false)
	private Set<Role> roles = Sets.newHashSet();
	
	@TableField(exist=false)
	private Set<Menu> menus = Sets.newHashSet();

	@TableField(exist=false)
	private Set<PermissionVO> permissions = Sets.newHashSet();


	@JSONField(serialize=false)
	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "User{" +
			", loginName=" + loginName +
			", nickName=" + nickName +
			", password=" + password +
			", salt=" + salt +
			", tel=" + tel +
			", email=" + email +
			", locked=" + locked +
			", icon=" + icon +
			"}";
	}
}
