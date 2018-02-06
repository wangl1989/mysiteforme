package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.entity.BlogArticle;
import com.baomidou.mybatisplus.service.IService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 博客内容 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
public interface BlogArticleService extends IService<BlogArticle> {

    /**
     * 移除文章跟栏目的关系
     * @param channelId 栏目ID
     */
    void removeArticleChannel(Long channelId);

    /**
     * 查询文章的详细数据(关联表查询)
     * @param id 文章ID
     * @return
     */
    BlogArticle selectOneDetailById(Long id);


    /**
     * 分页查询文章列表
     * @param map
     * @param page
     * @return
     */
    Page<BlogArticle> selectDetailArticle(Map<String,Object> map,Page<BlogArticle> page);

    /**
     * 不分页查询文章数据
     * @param map 参数
     * @return
     */
    List<BlogArticle> selectDetailArticle(Map<String,Object> map);

    /**
     * 根据条件查询文章数据(不关联表查询)
     * @param paramMap 参数Map
     * @return
     */
    List<BlogArticle> selectArticleData(Map<String, Object> paramMap);

    /**
     * 查询首页文章数据(关联表查询)
     * @param paramMap 参数Map
     * @return
     */
    List<BlogArticle> selectBlogIndexArticles(Map<String,Object> paramMap);

    /**
     * 这里统一更新或者新增数据
     * @param blogArticle 文章对象
     */
    BlogArticle saveOrUpdateArticle(BlogArticle blogArticle);

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
     * @param articleId
     * @return
     */
    Integer getArticleClick(Long articleId);

    /**
     * 刷新文章的点击量(+1)
     * @param articleId
     * @return
     */
    Integer flashArticleClick(Long articleId);

    /**
     * 创建文章搜索索引
     */
    void createArticlIndex();

    /**
     * 获取时间轴的文章
     * @param id
     * @return
     */
    List<BlogArticle> selectTimeLineList(Long id);

    /**
     * 查询最新评论的文章
     * @param limit
     * @return
     */
    List<BlogArticle> selectNewCommentArticle(Integer limit);
}
