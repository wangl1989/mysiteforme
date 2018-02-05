package com.mysiteforme.admin.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.DataEntity;

/**
 * <p>
 * 博客内容
 * </p>
 *
 * @author wangl
 * @since 2018-01-18
 */
@TableName("blog_article")
public class BlogArticle extends DataEntity<BlogArticle> {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
	private String title;
    /**
     * 副标题
     */
	@TableField("sub_title")
	private String subTitle;
    /**
     * 摘要
     */
	private String marks;
    /**
     * 显示图片
     */
	@TableField("show_pic")
	private String showPic;
    /**
     * 文章类型
     */
	private String category;
    /**
     * 外链地址
     */
	@TableField("out_link_url")
	private String outLinkUrl;
    /**
     * 来源
     */
	private String resources;
    /**
     * 发布时间
     */
	@TableField("publist_time")
	private Date publistTime;
    /**
     * 内容
     */
	private String content;

	/**
	 * 纯文字文章内容
	 */
	private String text;
    /**
     * 浏览量
     */
	private Integer click;
    /**
     * 栏目ID
     */
	@TableField("channel_id")
	private Long channelId;
    /**
     * 排序值
     */
	private Integer sort;
    /**
     * 是否置顶
     */
	@TableField("is_top")
	private Boolean isTop;
    /**
     * 是否推荐
     */
	@TableField("is_recommend")
	private Boolean isRecommend;
    /**
     * 文章状态(默认状态0  1已审核  2审核不通过)
     */
	private Integer status;

	@TableField(exist = false)
	private BlogChannel blogChannel;

	@TableField(exist = false)
	private List<BlogTags> blogTags;

	@TableField(exist = false)
	private Integer commentCount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}
	public String getShowPic() {
		return showPic;
	}

	public void setShowPic(String showPic) {
		this.showPic = showPic;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	public String getOutLinkUrl() {
		return outLinkUrl;
	}

	public void setOutLinkUrl(String outLinkUrl) {
		this.outLinkUrl = outLinkUrl;
	}
	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}
	public Date getPublistTime() {
		return publistTime;
	}

	public void setPublistTime(Date publistTime) {
		this.publistTime = publistTime;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getClick() {
		return click;
	}

	public void setClick(Integer click) {
		this.click = click;
	}
	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Boolean getTop() {
		return isTop;
	}

	public void setTop(Boolean isTop) {
		this.isTop = isTop;
	}
	public Boolean getRecommend() {
		return isRecommend;
	}

	public void setRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BlogChannel getBlogChannel() {
		return blogChannel;
	}

	public void setBlogChannel(BlogChannel blogChannel) {
		this.blogChannel = blogChannel;
	}

	public List<BlogTags> getBlogTags() {
		return blogTags;
	}

	public void setBlogTags(List<BlogTags> blogTags) {
		this.blogTags = blogTags;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	@Override
	public String toString() {
		return "BlogArticle{" +
			", title=" + title +
			", subTitle=" + subTitle +
			", marks=" + marks +
			", showPic=" + showPic +
			", category=" + category +
			", outLinkUrl=" + outLinkUrl +
			", resources=" + resources +
			", publistTime=" + publistTime +
			", content=" + content +
			", click=" + click +
			", channelId=" + channelId +
			", sort=" + sort +
			", isTop=" + isTop +
			", isRecommend=" + isRecommend +
			", status=" + status +
			"}";
	}
}
