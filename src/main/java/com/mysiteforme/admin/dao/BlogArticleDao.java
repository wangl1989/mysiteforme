package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.entity.BlogArticle;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客内容 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
public interface BlogArticleDao extends BaseMapper<BlogArticle> {

    List<BlogArticle> selectIndexArticle(Map<String,Object> map);

    List<BlogArticle> selectDetailArticle(Map<String, Object> map, Page<BlogArticle> page);

    List<BlogArticle> selectDetailArticle(Map<String, Object> map);

    List<BlogArticle> selectNewCommentArticle();

    void saveArticleTags(Map<String, Object> map);

    void removeArticleTags(Long articleId);
}
