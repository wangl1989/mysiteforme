/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:27:53
 * @ Description: 博客评论Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.BlogComment;

public interface BlogCommentService extends IService<BlogComment> {

    /**
     * 根据文章ID查找最大楼层
     * @param articleId 文章ID
     * @return 最大楼层数
     */
    Integer getMaxFloor(Long articleId);

    /**
     * 根据回复ID查找最大楼层
     * @param replyId 回复ID
     * @return 最大楼层数
     */
    Integer getMaxFloorByReply(Long replyId);

    /**
     * 不采用mybatisPlus自动分页获取文章评论
     * @param articleId 文章ID
     * @param type 评论类型
     * @param page 分页参数
     * @return 评论分页数据
     */
    IPage<BlogComment> getArticleComments(Long articleId, Integer type, IPage<BlogComment> page);

    /**
     * 保存或更新博客评论
     * @param blogComment 评论对象
     */
    void saveOrUpdateBlogComment(BlogComment blogComment);

    /**
     * 获取文章评论数量
     * @param articleId 文章ID
     * @return 评论数量
     */
    Integer getArticleCommentsCount(Long articleId);
}
