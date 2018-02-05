package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.entity.BlogComment;
import com.baomidou.mybatisplus.service.IService;
/**
 * <p>
 * 博客评论 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
public interface BlogCommentService extends IService<BlogComment> {

    public Integer getMaxFloor(Long articleId);

    Page<BlogComment> getArticleComments(Long articleId,Page<BlogComment> page);

}
