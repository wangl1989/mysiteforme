/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 22:56:48
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 23:29:49
 * @ Description: 权限VO
 */

package com.mysiteforme.admin.entity.VO;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionVO {

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
     * 权限所在的组别
     */
    private PermissionGroupVO group;

    /**
     * 权限按钮集合
     */
    private List<PermissionButtonVO> buttons;

    /**
     * 权限api集合
     */
    private List<PermissionApiVO> apis;

    /**
     * 权限路由地址集合
     */
    private List<PermissionPageVO> pages;

    
}
