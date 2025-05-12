/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:51:44
 * @ Description: 菜单基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;


@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Data
public class Menu extends TreeEntity<Menu> {

    @Serial
	private static final long serialVersionUID = 1L;

    private String name;

    private String icon;

	/**
	 * 图标颜色
	 */
	private String color;
	/**
     * 链接地址
     */
	private String href;
    /**
     * 打开方式
     */
	private String target;
    /**
     * 是否显示
     */
	@TableField(value="is_show")
	private Boolean isShow;

	@TableField("bg_color")
	private String bgColor;
    /**
     * 权限标识
     */
	private String permission;


	private String path;

	/**
	 * 组件路径
	 */
	private String component;

	/**
	 * 菜单标题
	 */
	private String title;

	/**
	 * 是否显示徽标（菜单右侧的红色小圆点）
	 */
	@TableField("show_badge")
	private Boolean showBadge;

	/**
	 * 是否显示新徽标（菜单右侧的红色小字提醒标签）
	 */
	@TableField("show_text_badge")
	private String showTextBadge;

	/**
	 * 是否在菜单中隐藏（在左侧菜单栏中不显示）
	 */
	@TableField("is_hide")
	private Boolean isHide;

	/**
	 * 是否在标签页中隐藏 （在顶部标签栏中不显示    ）
	 */
	@TableField("is_hide_tab")
	private Boolean isHideTab;

	/**
	 * 链接地址
	 */
	private String link;

	/**
	 * 是否为iframe
	 */
	@TableField("is_iframe")
	private Boolean isIframe;

	/**
	 * 是否缓存
	 */
	@TableField("keep_alive")
	private Boolean keepAlive;

	/**
	 * 是否在主容器中
	 */
	@TableField("is_in_main_container")
	private Boolean isInMainContainer;

	@TableField(exist = false)
	private Integer dataCount;

	@Override
	public String toString() {
		return "Menu{" +
			", name=" + name +
			", icon=" + icon +
			", href=" + href +
			", target=" + target +
			", isShow=" + isShow +
			", bgColor=" + bgColor +
			", permission=" + permission +
			", path=" + path +
			", component=" + component +
			", title=" + title +
			", showBadge=" + showBadge +
			", showTextBadge=" + showTextBadge +
			", isHide=" + isHide +
			", isHideTab=" + isHideTab +
			", link=" + link +
			", isIframe=" + isIframe +
			", keepAlive=" + keepAlive +
			", isInMainContainer=" + isInMainContainer +
			"}";
	}
}
