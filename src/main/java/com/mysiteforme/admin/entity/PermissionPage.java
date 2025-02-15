/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:52:48
 * @ Description: 前端页面路由权限
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission_page")
public class PermissionPage extends DataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联权限表ID
     */
	@TableField("permission_id")
	private Long permissionId;
    /**
     * 页面URL
     */
	@TableField("page_url")
	private String pageUrl;
    /**
     * 排序
     */
	@TableField("sort")
	private Short sort;



}
