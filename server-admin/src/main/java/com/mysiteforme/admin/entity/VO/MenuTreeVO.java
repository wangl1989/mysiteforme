package com.mysiteforme.admin.entity.VO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MenuTreeVO implements Serializable{
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Long id;
    /**
     * 菜单名称
     */             
    private String name;
    /**
     * 父级ID   
     */
    private Long parentId;
    /**
     * 父级ID集合
     */
    private String parentIds;
    /**
     * 菜单级别
     */
    private String level;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 备注
     */ 
    private String remarks;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 菜单元数据
     */
    private MenuMetaVO meta;
    
    /**
     * 子菜单
     */
    private List<MenuTreeVO> children;
}
