package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.BlogTags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客标签 Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Mapper
public interface BlogTagsDao extends BaseMapper<BlogTags> {

    /**
     * 根据栏目ID获取标签集合
     */
    List<BlogTags> getTagsByChannelId(Long channelId);

    /**
     * 根据文章ID获取标签集合
     */
    List<BlogTags> getTagsByArticleId(Long articleId);

    /**
     * 删除跟这个标签相关的所有关系
     * @param tagId 标签ID
     */
    void removeArticleTagsByTagId(Long tagId);

    /**
     * 根据删选条件获取博客标签的分页列表
     */
    List<BlogTags> selectTagsPage(@Param("params") Map<String, Object> map, IPage<BlogTags> page);

    List<BlogTags> selectTagsPage(@Param("params") Map<String, Object> map);
}
