package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.BlogTags;
import com.mysiteforme.admin.dao.BlogTagsDao;
import com.mysiteforme.admin.service.BlogTagsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客标签 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BlogTagsServiceImpl extends ServiceImpl<BlogTagsDao, BlogTags> implements BlogTagsService {

    /**
     * 根据标签名称获取标签数量
     * @param name 标签名称
     * @return 标签数量
     */
    @Override
    public Long getCountByName(String name) {
        QueryWrapper<BlogTags> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return count(wrapper);
    }

    /**
     * 保存标签信息
     * 自动设置排序值为当前最大排序值+1
     * @param tags 要保存的标签对象
     */
    @Override
    public void saveTag(BlogTags tags) {
        QueryWrapper<BlogTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.eqSql("sort","select max(sort) from blog_tags ")
                .eq("del_flag",false);
        BlogTags blogTags = getOne(queryWrapper);
        int sort = 0;
        if(blogTags != null){
            sort =  blogTags.getSort() + 1;
        }
        tags.setSort(sort);
        save(tags);
    }

    /**
     * 获取所有未删除的标签列表
     * 按照sort字段降序排序
     * @return 标签列表
     */
    @Override
    public List<BlogTags> listAll() {
        QueryWrapper<BlogTags> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.orderBy(false,false,"sort");
        return list(wrapper);
    }

    /**
     * 根据栏目ID获取标签列表
     * 结果会被缓存
     * @param channelId 栏目ID
     * @return 标签列表
     */
    @Cacheable(value = "blogTagsData",key = "'blog_tags_channel_'+#channelId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogTags> getTagsByChannelId(Long channelId) {
        return baseMapper.getTagsByChannelId(channelId);
    }

    /**
     * 根据文章ID获取标签列表
     * 结果会被缓存
     * @param articleId 文章ID
     * @return 标签列表
     */
    @Cacheable(value = "blogTagsData",key = "'blog_tags_article_'+#articleId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogTags> getTagsByArticleId(Long articleId) {
        return baseMapper.getTagsByArticleId(articleId);
    }

    /**
     * 删除指定标签及其关联的文章标签关系
     * 同时清除标签相关的所有缓存
     * @param id 标签ID
     */
    @CacheEvict(value = "blogTagsData",allEntries = true)
    @Override
    public void deleteThisTag(Long id) {
        removeById(id);
        baseMapper.removeArticleTagsByTagId(id);
    }

    /**
     * 分页查询标签列表
     * @param map 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    @Override
    public IPage<BlogTags> selectTagsPage(Map<String, Object> map, IPage<BlogTags> page) {
        List<BlogTags> blogTagsList = baseMapper.selectTagsPage(map,page);
        page.setRecords(blogTagsList);
        return page;
    }

    /**
     * 根据条件查询标签列表
     * @param map 查询条件
     * @return 标签列表
     */
    @Override
    public List<BlogTags> selectTagsPage(Map<String, Object> map) {
        return baseMapper.selectTagsPage(map);
    }
}
