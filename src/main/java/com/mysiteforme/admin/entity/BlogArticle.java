package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 *
 * 博客文章
 * </p>
 *
 * @author 昵称
 * @since 2025-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("blog_article")
public class BlogArticle extends DataEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章副标题
     */
    private String subTitle;
    /**
     * 摘要
     */
    private String marks;
    /**
     * 显示图片
     */
    private String showPic;
    /**
     * 文章类型
     */
    private String category;
    /**
     * 外链地址
     */
    private String outLinkUrl;
    /**
     * 来源
     */
    private String resources;
    /**
     * 发布时间
     */
    private LocalDateTime publistTime;
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
    private Long channelId;

    @TableField(exist = false)
    private BlogChannel blogChannel;

    @TableField(exist = false)
    private List<BlogTags> blogTags;
    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 是否置顶
     */
    private Boolean isTop;
    /**
     * 是否推荐
     */
    private Boolean isRecommend;
    /**
     * 文章状态
     */
    private Integer status;


}