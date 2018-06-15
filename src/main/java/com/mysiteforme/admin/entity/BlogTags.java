package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.DataEntity;

/**
 * <p>
 * 博客标签
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@TableName("blog_tags")
public class BlogTags extends DataEntity<BlogTags> {

    private static final long serialVersionUID = 1L;

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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getTagsUseCount() {
		return tagsUseCount;
	}

	public void setTagsUseCount(Integer tagsUseCount) {
		this.tagsUseCount = tagsUseCount;
	}

	@Override
	public String toString() {
		return "BlogTags{" +
			", name=" + name +
			", sort=" + sort +
			", tagsUseCount=" + tagsUseCount +
			"}";
	}
}
