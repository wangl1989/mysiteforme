/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:27:30
 * @ Description: 博客文章Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.BlogArticle;

import java.util.List;
import java.util.Map;

public interface BlogArticleService extends IService<BlogArticle> {

    /**
     * 移除文章与栏目的关联关系
     * @param channelId 栏目ID
     */
    void removeArticleChannel(Long channelId);

    /**
     * 查询文章的详细数据(关联表查询)
     * @param id 文章ID
     */
    BlogArticle selectOneDetailById(Long id);


    /**
     * 分页查询文章列表
     */
    IPage<BlogArticle> selectDetailArticle(Map<String,Object> map, IPage<BlogArticle> page);

    /**
     * 查询文章详情列表
     * @param map 查询条件
     * @return 文章列表
     */
    List<BlogArticle> selectDetailArticle(Map<String, Object> map);

    /**
     * 根据栏目ID查询文章数据
     * @param paramMap 包含channelId和limit的参数Map
     * @return 文章列表
     */
    List<BlogArticle> selectArticleData(Map<String, Object> paramMap);

    /**
     * 查询首页文章列表
     * @param paramMap 包含limit和order的参数Map
     * @return 文章列表
     */
    List<BlogArticle> selectBlogIndexArticles(Map<String, Object> paramMap);

    /**
     * 这里统一更新或者新增数据
     *
     * @param blogArticle 文章对象
     */
    void saveOrUpdateArticle(BlogArticle blogArticle);

    /**
     * 保存文章标签的关系
     * @param map map数据{"articleId":文章ID,"tags":标签List}
     */
    void saveArticleTags(Map<String, Object> map);

    /**
     * 删除文章标签的关系
     * @param articleId 文章ID
     */
    void removeArticleTags(Long articleId);

    /**
     * 获取文章点击数量
     */
    Integer getArticleClick(Long articleId);

    /**
     * 更新文章点击数
     * @param articleId 文章ID
     * @return 更新后的点击数
     */
    Integer flashArticleClick(Long articleId);

    /**
     * 创建文章搜索索引
     * 用于全文检索
     */
    void createArticlIndex();

    /**
     * 获取时间轴的文章列表
     * @param id 用户ID
     * @return 文章列表
     */
    List<BlogArticle> selectTimeLineList(Long id);

    /**
     * 查询最新评论的文章列表
     * @param limit 限制数量
     * @return 文章列表
     */
    List<BlogArticle> selectNewCommentArticle(Integer limit);

    /**
     * 查询与当前文章标签相似的文章列表
     * @param map 包含articleId和limit的参数Map
     * @return 相似文章列表
     */
    List<BlogArticle> selectLikeSameWithTags(Map<String,Object> map);
}
