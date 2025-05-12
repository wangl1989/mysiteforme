/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:50:50
 * @ Description: 博客标签基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("blog_tags")
@Data
public class BlogTags extends DataEntity {

    /**
     * 标签名字
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;

	@TableField(exist = false)
	private Integer tagsUseCount;

	@Override
	public String toString() {
		return "BlogTags{" +
			", name=" + name +
			", sort=" + sort +
			", tagsUseCount=" + tagsUseCount +
			"}";
	}
}
