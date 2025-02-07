package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.dao.BlogChannelDao;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.service.BlogChannelService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客栏目 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BlogChannelServiceImpl extends ServiceImpl<BlogChannelDao, BlogChannel> implements BlogChannelService {

    private static final Logger logger = LoggerFactory.getLogger(BlogChannelServiceImpl.class);

    @Cacheable(value = "channelData",key = "'articleZtree'",unless = "#result == null or #result.size() == 0")
    @Override
    public List<ZtreeVO> selectZtreeData() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("pid",null);
        return baseMapper.selectZtreeData(map);
    }

    @Cacheable(value = "channelData",key = "'channelList'",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogChannel> selectChannelList() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("parentId",null);
        List<BlogChannel> list = Lists.newArrayList();
        try {
            list = baseMapper.selectChannelData(map);
        }catch (Exception e){
            logger.error("查询栏目列表失败",e);
        }
        return list;
    }

    @Caching(evict = {
            @CacheEvict(value = "channelData",allEntries = true),
            @CacheEvict(value = "myarticle",allEntries = true),
            @CacheEvict(value = "oneArticle",allEntries = true),
            @CacheEvict(value = "blogTagsData",allEntries = true)
    })
    @Override
    public void saveOrUpdateChannel(BlogChannel blogChannel) {
        try {
            saveOrUpdate(blogChannel);
        }catch (Exception e){
            logger.error("保存或更新栏目失败",e);
        }
    }

    @Override
    public long getCountByName(String name) {
        QueryWrapper<BlogChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        wrapper.eq("del_flag",false);
        return count(wrapper);
    }

    @Cacheable(value = "channelData",key = "'blog_channel_top_limit'+#limit",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogChannel> getChannelListByWrapper(int limit, QueryWrapper<BlogChannel> wrapper) {
        return list(new Page<>(1,limit),wrapper);
    }

    @Cacheable(value = "channelData",key = "'blog_parent_channel_list_'+#channelId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogChannel> getParentsChannel(Long channelId) {
        QueryWrapper<BlogChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("id",channelId);
        BlogChannel blogChannel = getOne(wrapper);
        if(blogChannel == null){
            return null;
        }
        String[] parentIds = blogChannel.getParentIds().split(",");
        List<Long> ids = Lists.newArrayList();
        for (String parentId : parentIds) {
            ids.add(Long.valueOf(parentId));
        }
        return listByIds(ids);
    }

    @Override
    public BlogChannel getChannelByHref(String href) {
        QueryWrapper<BlogChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("href",href);
        return getOne(wrapper);
    }
}
