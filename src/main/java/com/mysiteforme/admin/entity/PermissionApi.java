/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:52:03
 * @ Description: 权限API基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission_api")
public class PermissionApi extends DataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联权限表ID
     */
	@TableField("permission_id")
	private Long permissionId;
    /**
     * API接口URL
     */
	@TableField("api_url")
	private String apiUrl;
    /**
     * HTTP请求方法
     */
	@TableField("http_method")
	private String httpMethod;
    /**
     * 排序
     */
	@TableField("sort")
	private Short sort;



}
