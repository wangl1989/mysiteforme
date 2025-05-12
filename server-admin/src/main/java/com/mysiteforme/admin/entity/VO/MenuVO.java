package com.mysiteforme.admin.entity.VO;

import java.io.Serializable;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class MenuVO implements Serializable{

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

    
    
}
