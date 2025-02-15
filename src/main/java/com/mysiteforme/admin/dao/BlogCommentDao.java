/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:40:33
 * @ Description: 博客评论数据层接口 提供博客评论的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.BlogComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface BlogCommentDao extends BaseMapper<BlogComment> {

    /**
     * 查询文章评论 手动分页
     */
    List<BlogComment> selectArticleComments(Map<String, Object> map);

    Integer selectArticleCommentsCount(Map<String, Object> map);

    List<BlogComment> selectArticleCommentsByPlus(Map<String, Object> map, IPage<BlogComment> page);

    List<BlogComment> getCommentByReplyId(Long replyId);

}
