/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:51:04
 * @ Description: 字典表基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@TableName("sys_dict")
public class Dict extends DataEntity {
    /**
     * 数据值
     */
	private String value;
    /**
     * 标签名
     */
	private String label;
    /**
     * 类型
     */
	private String type;
    /**
     * 描述
     */
	private String description;
    /**
     * 排序（升序）
     */
	private Integer sort;
    /**
     * 父级编号
     */
	@TableField("parent_id")
	private String parentId;

	@Override
	public String toString() {
		return "Dict{" +
			", value=" + value +
			", label=" + label +
			", type=" + type +
			", description=" + description +
			", sort=" + sort +
			", parentId=" + parentId +
			"}";
	}
}
