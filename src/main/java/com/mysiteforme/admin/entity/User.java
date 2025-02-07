package com.mysiteforme.admin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Sets;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Data
@Getter
@Setter
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
	private Set<Role> roleLists = Sets.newHashSet();
	
	@TableField(exist=false)
	private Set<Menu> menus = Sets.newHashSet();


	@JSONField(serialize=false)
	public String getPassword() {
		return password;
	}


	@JSONField(serialize=false)
	public String getSalt() {
		return salt;
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
