/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:47:14
 * @ Description: 博客栏目前端展示对象
 */

package com.mysiteforme.admin.entity.VO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Data
public class BlogChannelVO implements Serializable {

    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    /**
     * 父节点ID
     */
    private Long parentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 站点ID
     */
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
    private Boolean baseChannel;
    /**
     * 是否能够评论
     */
    private Boolean canComment;
    /**
     * 是否匿名
     */
    private Boolean noName;
    /**
     * 是否开启审核
     */
    private Boolean canAduit;

    /**
     * 父节点ID路径
     */
    private String parentIds;
    /**
     * 网页title(seo)
     */
    private String seoTitle;
    /**
     * 网页关键字(seo)
     */
    private String seoKeywords;
    /**
     * 网页描述(seo)
     */
    private String seoDescription;

    /**
     * 节点层次（第一层，第二层，第三层....）
     */
    private Integer level;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 子节点channel
     */
    private List<BlogChannelVO> children;
}
