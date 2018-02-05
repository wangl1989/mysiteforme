package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mysiteforme.admin.base.DataEntity;

/**
 * <p>
 * 博客评论
 * </p>
 *
 * @author wangl
 * @since 2018-01-18
 */
@TableName("blog_comment")
public class BlogComment extends DataEntity<BlogComment> {

    private static final long serialVersionUID = 1L;

    /**
     * 评论内容
     */
	private String content;

	/**
	 * 评论类型：1.文章评论，2.系统留言
	 */
	private Integer type;
    /**
     * ip
     */
	private String ip;
    /**
     * 操作系统
     */
	private String system;
    /**
     * 浏览器
     */
	private String browser;
    /**
     * 楼层
     */
	private Integer floor;
    /**
     * 栏目ID
     */
	@TableField("channel_id")
	private Long channelId;
    /**
     * 文章ID
     */
	@TableField("article_id")
	private Long articleId;
    /**
     * 回复评论ID
     */
	@TableField("reply_id")
	private Long replyId;
    /**
     * 管理员是否回复
     */
	@TableField("is_admin_reply")
	private Boolean isAdminReply;
    /**
     * 管理员回复内容
     */
	@TableField("reply_content")
	private String replyContent;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}
	public Boolean getAdminReply() {
		return isAdminReply;
	}

	public void setAdminReply(Boolean isAdminReply) {
		this.isAdminReply = isAdminReply;
	}
	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}


	@Override
	public String toString() {
		return "BlogComment{" +
			", content=" + content +
			", ip=" + ip +
			", system=" + system +
			", browser=" + browser +
			", floor=" + floor +
			", channelId=" + channelId +
			", articleId=" + articleId +
			", replyId=" + replyId +
			", isAdminReply=" + isAdminReply +
			", replyContent=" + replyContent +
			"}";
	}
}
