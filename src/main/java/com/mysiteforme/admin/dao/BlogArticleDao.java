/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:40:00
 * @ Description: 博客文章数据层接口 提供博客文章的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.BlogArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface BlogArticleDao extends BaseMapper<BlogArticle> {

    List<BlogArticle> selectIndexArticle(Map<String,Object> map);

    List<BlogArticle> selectDetailArticle(@Param("params") Map<String, Object> map, IPage<BlogArticle> page);

    List<BlogArticle> selectDetailArticle(Map<String, Object> map);

    List<BlogArticle> selectNewCommentArticle(Integer limit);

    /**
     * 查找当前文章的标签相似的文章
     */
    List<BlogArticle> selectLikeSameWithTags(Map<String,Object> map);

    void saveArticleTags(Map<String, Object> map);

    void removeArticleTags(Long articleId);
}
