package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 博客标签
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@EqualsAndHashCode(callSuper = true)
@TableName("blog_tags")
@Data
@Getter
@Setter
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
