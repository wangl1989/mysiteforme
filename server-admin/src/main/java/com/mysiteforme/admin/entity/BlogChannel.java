/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:50:26
 * @ Description: 博客栏目基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("blog_channel")
public class BlogChannel extends TreeEntity<BlogChannel> {

    /**
     * 名称
     */
	private String name;
    /**
     * 站点ID
     */
	@TableField("site_id")
	private Long siteId;

	/**
	 * 链接地址
	 */
	private String href;
    /**
     * 栏目图标
     */
	private String logo;
    /**
     * 该栏目文章是否可以在首页展示
     */
	@TableField("is_base_channel")
	private Boolean isBaseChannel;
    /**
     * 是否能够评论
     */
	@TableField("can_comment")
	private Boolean canComment;
    /**
     * 是否匿名
     */
	@TableField("is_no_name")
	private Boolean isNoName;
    /**
     * 是否开启审核
     */
	@TableField("is_can_aduit")
	private Boolean isCanAduit;
    /**
     * 网页title(seo)
     */
	@TableField("seo_title")
	private String seoTitle;
    /**
     * 网页关键字(seo) 
     */
	@TableField("seo_keywords")
	private String seoKeywords;
    /**
     * 网页描述(seo)
     */
	@TableField("seo_description")
	private String seoDescription;


	@Override
	public String toString() {
		return "BlogChannel{" +
			", name=" + name +
			", siteId=" + siteId +
			", logo=" + logo +
			", isBaseChannel=" + isBaseChannel +
			", canComment=" + canComment +
			", isNoName=" + isNoName +
			", isCanAduit=" + isCanAduit +
			", seoTitle=" + seoTitle +
			", seoKeywords=" + seoKeywords +
			", seoDescription=" + seoDescription +
			"}";
	}
}
