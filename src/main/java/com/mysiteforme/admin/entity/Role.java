/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:53:25
 * @ Description: 角色基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Data
public class Role extends DataEntity {

    /**
     * 角色名称
     */
	private String name;

	/**
	 * 是否为默认角色
	 */
	@TableField("is_default")
	private Boolean isDefault;

	@TableField(exist = false)
	private Set<Menu> menuSet;
	
	@TableField(exist = false)
	private Set<Permission> permissionSet;

	@TableField(exist = false)
	private Set<User> userSet;

	public Role(){
		super();
	}

	public Role(Long id){
		this.id = id;
	}

}
