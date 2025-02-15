/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 22:45:53
 * @ Description: 按钮权限
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission_button")
public class PermissionButton extends DataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联权限表ID
     */
	@TableField("permission_id")
	private Long permissionId;
    /**
     * 按钮标识
     */
	@TableField("button_key")
	private String buttonKey;
    /**
     * 按钮名称
     */
	@TableField("button_name")
	private String buttonName;
    /**
     * 排序
     */
	@TableField("sort")
	private Short sort;



}
