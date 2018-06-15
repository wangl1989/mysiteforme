package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.entity.BlogTags;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客标签 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
public interface BlogTagsService extends IService<BlogTags> {

    /**
     * 根据标签名称获取数量
     * @param name 标签名称
     * @return
     */
    Integer getCountByName(String name);

    /**
     * 新增标签
     * @param tags
     */
    void saveTag(BlogTags tags);

    /**
     * 获取所有标签
     * @return
     */
    List<BlogTags> listAll();

    /**
     * 获取栏目对应的文章的所有标签
     * @param channelId 栏目ID
     * @return
     */
    List<BlogTags> getTagsByChannelId(Long channelId);

    /**
     * 获取文章的所有标签
     * @param articleId 文章ID
     * @return
     */
    List<BlogTags> getTagsByArticleId(Long articleId);

    void deleteThisTag(Long id);

    Page<BlogTags> selectTagsPage(Map<String, Object> map, Page<BlogTags> page);

    List<BlogTags> selectTagsPage(Map<String, Object> map);
}
