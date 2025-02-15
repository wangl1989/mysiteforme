package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Data
@Getter
@Setter
public class Menu extends TreeEntity<Menu> {

    private static final long serialVersionUID = 1L;

    private String name;

    private String icon;
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

	@TableField(exist = false)
	private Integer dataCount;


	public String getIcon() {
		return icon;
	}

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
			"}";
	}
}
