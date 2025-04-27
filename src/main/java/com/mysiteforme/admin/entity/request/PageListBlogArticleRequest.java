package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 *
 *  博客文章 分页列表请求参数对象
 * </p>
 *
 * @author 昵称
 * @since 2025-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageListBlogArticleRequest extends BasePageRequest {
    /**
     * 外链地址
     */
    private String outLinkUrl;
    /**
     * 来源
     */
    private String resources;
    /**
     * 栏目ID
     */
    private Long channelId;
    /**
     * 创建时间排序
     */
    private Boolean sortByCreateDateAsc;


}