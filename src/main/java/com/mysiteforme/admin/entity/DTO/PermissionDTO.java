/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:46:15
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 12:42:17
 * @ Description: 权限数据传输对象
 */

package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.Permission;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionDTO extends Permission {

    private Integer page = 1;
    private Integer limit = 10;
    
    /**
     * 权限类型
     */
    private Integer type;
    
    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * API权限数据
     */
    private PermissionApiDTO api;
    
    /**
     * 页面权限数据
     */
    private PermissionPageDTO pagePermission;
    
    /**
     * 按钮权限数据
     */
    private PermissionButtonDTO button;

}
