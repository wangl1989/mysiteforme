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

    @Override
    public Integer getCountByName(String name) {
        QueryWrapper<BlogTags> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return Math.toIntExact(count(wrapper));
    }

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

    @Override
    public List<BlogTags> listAll() {
        QueryWrapper<BlogTags> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.orderBy(false,false,"sort");
        return list(wrapper);
    }

    @Cacheable(value = "blogTagsData",key = "'blog_tags_channel_'+#channelId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogTags> getTagsByChannelId(Long channelId) {
        return baseMapper.getTagsByChannelId(channelId);
    }

    @Cacheable(value = "blogTagsData",key = "'blog_tags_article_'+#articleId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogTags> getTagsByArticleId(Long articleId) {
        return baseMapper.getTagsByArticleId(articleId);
    }

    @CacheEvict(value = "blogTagsData",allEntries = true)
    @Override
    public void deleteThisTag(Long id) {
        removeById(id);
        baseMapper.removeArticleTagsByTagId(id);
    }

    @Override
    public IPage<BlogTags> selectTagsPage(Map<String, Object> map, IPage<BlogTags> page) {
        List<BlogTags> blogTagsList = baseMapper.selectTagsPage(map,page);
        page.setRecords(blogTagsList);
        return page;
    }

    @Override
    public List<BlogTags> selectTagsPage(Map<String, Object> map) {
        return baseMapper.selectTagsPage(map);
    }
}
