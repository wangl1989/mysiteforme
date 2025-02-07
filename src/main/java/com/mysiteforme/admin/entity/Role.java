package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_role")
@Getter
@Setter
@Data
public class Role extends DataEntity {

    /**
     * 角色名称
     */
	private String name;

	@TableField(exist = false)
	private Set<Menu> menuSet;

	@TableField(exist = false)
	private Set<User> userSet;


}
