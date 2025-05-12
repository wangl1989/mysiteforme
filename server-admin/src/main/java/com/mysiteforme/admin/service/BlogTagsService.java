/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:28:02
 * @ Description: 博客标签Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.BlogTags;

import java.util.List;
import java.util.Map;


public interface BlogTagsService extends IService<BlogTags> {

    /**
     * 根据标签名称获取数量
     * @param name 标签名称
     * @return 标签数量
     */
    Long getCountByName(String name);

    /**
     * 新增标签
     */
    void saveTag(BlogTags tags);

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<BlogTags> listAll();

    /**
     * 获取栏目对应的文章的所有标签
     * @param channelId 栏目ID
     * @return 标签列表
     */
    List<BlogTags> getTagsByChannelId(Long channelId);

    /**
     * 获取文章的所有标签
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<BlogTags> getTagsByArticleId(Long articleId);

    /**
     * 删除指定标签
     * @param id 标签ID
     */
    void deleteThisTag(Long id);

    /**
     * 分页查询标签
     * @param map 查询条件
     * @param page 分页参数
     * @return 标签分页数据
     */
    IPage<BlogTags> selectTagsPage(Map<String, Object> map, IPage<BlogTags> page);

    /**
     * 根据条件查询标签列表
     * @param map 查询条件
     * @return 标签列表
     */
    List<BlogTags> selectTagsPage(Map<String, Object> map);
}
