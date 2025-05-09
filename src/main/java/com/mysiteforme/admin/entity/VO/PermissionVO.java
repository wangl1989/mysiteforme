/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 22:56:48
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 21:16:37
 * @ Description: 权限VO
 */

package com.mysiteforme.admin.entity.VO;


import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionVO implements Serializable,Comparable<PermissionVO>{

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 权限名称
     */     
    private String permissionName;

    /** 
     * 权限编码 1. page_menu_list  2. button_user_add 3.api_user_add.
     */
    private String permissionCode;

    /**
     * 权限类型: 1.可访问页面地址(page) 2. 可显示按钮可显示按钮(button)    3. 可访问接口地址(api)
     */
    private Integer permissionType;

    /**
     * 权限图标
     */
    private String icon;

    /**
     * 图标颜色
     */
    private String color;

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
    private PermissionButtonVO button;

    /**
     * 权限api集合
     */
    private PermissionApiVO api;

    /**
     * 权限路由地址集合
     */
    private PermissionPageVO page;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;


    @Override
    public int compareTo(@NotNull PermissionVO p) {
        // 首先按照sort倒序
        int sortCompare = Integer.compare(p.getSort(), this.getSort());
        if (sortCompare != 0) {
            return sortCompare;
        }
        // sort相同时按照createDate倒序
        return p.getUpdateDate().compareTo(this.getUpdateDate());
    }
}
