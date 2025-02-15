/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:39:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:52:17
 * @ Description: 权限组别
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;


@SuppressWarnings("rawtypes")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission_group")
public class PermissionGroup extends TreeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分组编码
     */
	@TableField("group_code")
	private String groupCode;
    /**
     * 分组名称
     */
	@TableField("group_name")
	private String groupName;
    /**
     * 更新时间
     */
	@TableField("update_time")
	private LocalDateTime updateTime;



}
