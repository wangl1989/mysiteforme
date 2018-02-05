package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.dao.BlogChannelDao;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.service.BlogChannelService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            e.printStackTrace();
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
            insertOrUpdate(blogChannel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCountByName(String name) {
        EntityWrapper<BlogChannel> wrapper = new EntityWrapper<>();
        wrapper.eq("name",name);
        wrapper.eq("del_flag",false);
        return selectCount(wrapper);
    }

    @Cacheable(value = "channelData",key = "'blog_channel_top_limit'+#limit",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogChannel> getChannelListByWrapper(int limit, EntityWrapper<BlogChannel> wrapper) {
        return selectPage(new Page<>(1,limit),wrapper).getRecords();
    }

    @Cacheable(value = "channelData",key = "'blog_parent_channel_list_'+#channelId",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogChannel> getParentsChannel(Long channelId) {
        EntityWrapper<BlogChannel> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("id",channelId);
        BlogChannel blogChannel = selectOne(wrapper);
        String[] parentIds = blogChannel.getParentIds().split(",");
        List<Long> ids = Lists.newArrayList();
        for(int i=0;i<parentIds.length;i++){
            ids.add(Long.valueOf(parentIds[i]));
        }
        List<BlogChannel> channelList = selectBatchIds(ids);
        return channelList;
    }

    @Override
    public BlogChannel getChannelByHref(String href) {
        EntityWrapper<BlogChannel> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("href",href);
        BlogChannel c  = selectOne(wrapper);
        return c;
    }
}
