package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AddMenuRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    @NotNull(message = MessageConstants.Menu.MENU_NAME_EMPTY)
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
    private Integer level;

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
    @NotNull(message = MessageConstants.Menu.MENU_PATH_EMPTY)
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单标题
     */
    @NotNull(message = MessageConstants.Menu.MENU_TITLE_EMPTY)
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
     * 是否在主容器中
     */
    private Boolean isInMainContainer;


}
