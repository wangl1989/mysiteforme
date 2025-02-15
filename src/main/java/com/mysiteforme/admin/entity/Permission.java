/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 22:47:38
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:51:52
 * @ Description: 权限基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends DataEntity {
    /**
     * 权限名称
     */     
    private String permissionName;

    /** 
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型
     */
    private String permissionType;

	/**
	 * 分组ID
	 */
	private Long groupId;
    /**
     * 权限图标
     */
    private String icon;

    /**
     * 排序值
     */
    private Integer sort;
    
}   
