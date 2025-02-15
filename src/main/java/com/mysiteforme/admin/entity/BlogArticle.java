/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:50:14
 * @ Description: 博客基类
 */

package com.mysiteforme.admin.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Data
@TableName("blog_article")
public class BlogArticle extends DataEntity {


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

	/**
	 * 文章所属栏目
	 */
	@TableField(exist = false)
	private BlogChannel blogChannel;

	/**
	 * 文章标签集合
	 */
	@TableField(exist = false)
	private List<BlogTags> blogTags;

	/**
	 * 文章评论数
	 */
	@TableField(exist = false)
	private Integer commentCount;

	/**
	 * 当前文章最新评论时间
	 */
	@TableField(exist = false)
	private Date newestCommentTime;

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
