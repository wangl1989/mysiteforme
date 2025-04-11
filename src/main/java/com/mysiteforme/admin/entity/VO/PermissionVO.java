/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 22:56:48
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:16:37
 * @ Description: 权限VO
 */

package com.mysiteforme.admin.entity.VO;


import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionVO implements Serializable{

    private static final long serialVersionUID = 1L; 

    private Long id;
    /**
     * 权限名称
     */     
    private String permissionName;

    /** 
     * 权限编码 1. page_menu_list  2. api_user_add. 3.button_user_add
     */
    private String permissionCode;

    /**
     * 权限类型: 1.可访问页面地址(page) 2. 可访问接口地址(api)    3. 可显示按钮可显示按钮(button)
     */
    private String permissionType;

    /**
     * 权限图标
     */
    private String icon;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 菜单对象
     */
    private MenuVO menu;

    /**
     * 权限按钮集合
     */
    @Setter
    private PermissionButtonVO button;

    /**
     * 权限api集合
     */
    private PermissionApiVO api;

    /**
     * 权限路由地址集合
     */
    private PermissionPageVO page;

    
}
