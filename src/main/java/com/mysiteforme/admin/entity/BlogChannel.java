package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.TreeEntity;

/**
 * <p>
 * 博客栏目
 * </p>
 *
 * @author wangl
 * @since 2018-01-18
 */
@TableName("blog_channel")
public class BlogChannel extends TreeEntity<BlogChannel> {

    private static final long serialVersionUID = 1L;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Boolean getBaseChannel() {
		return isBaseChannel;
	}

	public void setBaseChannel(Boolean isBaseChannel) {
		this.isBaseChannel = isBaseChannel;
	}
	public Boolean getCanComment() {
		return canComment;
	}

	public void setCanComment(Boolean canComment) {
		this.canComment = canComment;
	}
	public Boolean getNoName() {
		return isNoName;
	}

	public void setNoName(Boolean isNoName) {
		this.isNoName = isNoName;
	}
	public Boolean getCanAduit() {
		return isCanAduit;
	}

	public void setCanAduit(Boolean isCanAduit) {
		this.isCanAduit = isCanAduit;
	}
	public String getSeoTitle() {
		return seoTitle;
	}

	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}
	public String getSeoKeywords() {
		return seoKeywords;
	}

	public void setSeoKeywords(String seoKeywords) {
		this.seoKeywords = seoKeywords;
	}
	public String getSeoDescription() {
		return seoDescription;
	}

	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}


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
