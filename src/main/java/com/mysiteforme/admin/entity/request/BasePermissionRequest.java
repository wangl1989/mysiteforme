package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;
import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BasePermissionRequest {

    /**
     * 权限名称
     */
    @NotBlank(message = MessageConstants.Permission.NAME_NOT_NULL)
    private String permissionName;

    /**
     * 权限编码
     */
    @NotBlank(message = MessageConstants.Permission.CODE_NOT_NULL)
    private String permissionCode;

    /**
     * 权限类型
     */
    @NotNull(message = MessageConstants.Permission.TYPE_NOT_NULL)
    private Integer permissionType;

    /**
     * 分组ID
     */
    @NotNull(message = MessageConstants.Permission.MENU_ID_NOT_NULL)
    private Long menuId;
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
