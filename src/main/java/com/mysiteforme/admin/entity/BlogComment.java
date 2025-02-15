/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:50:41
 * @ Description: 博客评论基类
 */

package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@TableName("blog_comment")
public class BlogComment extends DataEntity {

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

	@TableField(exist = false)
	private List<BlogComment> replayList;

	public Boolean getAdminReply() {
		return isAdminReply;
	}

	public void setAdminReply(Boolean isAdminReply) {
		this.isAdminReply = isAdminReply;
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
