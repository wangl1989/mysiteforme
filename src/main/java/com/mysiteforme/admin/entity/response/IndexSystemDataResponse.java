package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页用户系统数据
 */
@Data
public class IndexSystemDataResponse implements Serializable {

    private Long id;

    /**
     * 用户拥有角色总数
     */
    private Integer roleCount;

    /**
     * 用户拥有菜单总数
     */
    private Integer menuCount;

    /**
     * 用户拥有权限总数
     */
    private Integer permissionCount;

    /**
     * 用户角色占比百分比
     */
    private Integer rolePercent;

    /**
     * 用户菜单占比百分比
     */
    private Integer menuPercent;

    /**
     * 用户权限占比百分比
     */
    private Integer permissionPercent;
}
