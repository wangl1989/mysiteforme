package com.mysiteforme.admin.entity.VO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class MenuMetaVO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单标题 
     */
    private String title;
    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 图标颜色
     */
    private String color;
    /**
     * 是否显示徽标（菜单右侧的红色小圆点）
     */
    private Boolean showBadge;
    /** 
     * 是否显示新徽标（菜单右侧的红色小字提醒标签）
     */
    private String showTextBadge;
    /**
     * 是否在菜单中隐藏（在左侧菜单栏中不显示）
     */
    private Boolean isHide;
    /**
     * 是否在标签页中隐藏 （在顶部标签栏中不显示    ）
     */
    private Boolean isHideTab;
    /**
     * 链接地址
     */
    private String link;
    /**
     * 是否为iframe
     */
    private Boolean isIframe;
    /**
     * 是否缓存
     */
    private Boolean keepAlive;
    /**
     * 当前菜单拥有的菜单集合
     */
    private Set<PermissionVO> authList;

    /**
     * 按钮权限集合
     */
    private Set<String> buttonKeys;

    /**
     * 是否在主容器中
     */
    private Boolean isInMainContainer;
    
    
}
