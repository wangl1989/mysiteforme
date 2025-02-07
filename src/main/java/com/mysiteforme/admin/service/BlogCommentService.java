package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.BlogComment;
/**
 * <p>
 * 博客评论 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
public interface BlogCommentService extends IService<BlogComment> {

    /**
     * 根据文章ID查找最大楼层
     */
    Integer getMaxFloor(Long articleId);

    /**
     * 根据回复ID查找最大楼层
     */
    Integer getMaxFloorByReply(Long replyId);

    /**
     * 不采用mybatisPlus自动分页
     */
    IPage<BlogComment> getArticleComments(Long articleId, Integer type, IPage<BlogComment> page);



    void saveOrUpdateBlogComment(BlogComment blogComment);

    Integer getArticleCommentsCount(Long articleId);
}
