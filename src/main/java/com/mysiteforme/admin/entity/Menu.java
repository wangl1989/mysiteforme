package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.mysiteforme.admin.base.TreeEntity;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@TableName("sys_menu")
public class Menu extends TreeEntity<Menu> {

    private static final long serialVersionUID = 1L;

    private String name;

    private String icon;
	/**
     * 链接地址
     */
	@TableField(strategy= FieldStrategy.IGNORED)
	private String href;
    /**
     * 打开方式
     */
	@TableField(strategy= FieldStrategy.IGNORED)
	private String target;
    /**
     * 是否显示
     */
	@TableField(value="is_show",strategy= FieldStrategy.IGNORED)
	private Boolean isShow;

	@TableField("bg_color")
	private String bgColor;
    /**
     * 权限标识
     */
	@TableField(strategy= FieldStrategy.IGNORED)
	private String permission;

	@TableField(exist = false)
	private Integer dataCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min = 0, max = 1000, message = "icon长度必须介于 1 和 1000 之间")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getParentId() {
		return parentId;
	}


	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean show) {
		isShow = show;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}
}
