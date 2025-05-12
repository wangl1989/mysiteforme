package com.mysiteforme.admin.entity.request;

import java.time.LocalDateTime;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


/**
 * <p>
 *
 * 新增 博客文章 表单参数对象
 * </p>
 *
 * @author 昵称
 * @since 2025-04-27
 */
@Data
public class AddBlogArticleRequest {
    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
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
    @NotBlank(message = "内容不能为空")
    private String content;
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
     * 备注
     */
    private String remarks;



}