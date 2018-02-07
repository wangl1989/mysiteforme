package com.mysiteforme.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mysiteforme.admin.dao.BlogChannelDao;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.dao.BlogArticleDao;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.lucene.LuceneSearch;
import com.mysiteforme.admin.service.BlogArticleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mysiteforme.admin.service.BlogChannelService;
import org.apache.lucene.document.*;
import org.apache.shiro.cache.Cache;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static javafx.scene.input.KeyCode.V;

/**
 * <p>
 * 博客内容 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BlogArticleServiceImpl extends ServiceImpl<BlogArticleDao, BlogArticle> implements BlogArticleService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BlogChannelService blogChannelService;

    /***
     * 根据栏目ID清除文章与栏目的关系
     * @param channelId 栏目ID
     */
    @Override
    public void removeArticleChannel(Long channelId) {
        EntityWrapper<BlogArticle> wrapper = new EntityWrapper<>();
        wrapper.eq("channel_id",channelId);
        List<BlogArticle> list = selectList(wrapper);
        if(list.size()>0){
            for (BlogArticle blogArticle : list){
                blogArticle.setChannelId(null);
            }
            updateBatchById(list);
        }
    }

    @Cacheable(value = "oneArticle",key = "'article_id_'+#id",unless = "#result == null ")
    @Override
    public BlogArticle selectOneDetailById(Long id) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("id",id);
        List<BlogArticle> list = baseMapper.selectDetailArticle(map);
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<BlogArticle> selectDetailArticle(Map<String, Object> map, Page<BlogArticle> page) {
        List<BlogArticle> list = baseMapper.selectDetailArticle(map,page);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<BlogArticle> selectDetailArticle(Map<String, Object> map) {
        List<BlogArticle> list = baseMapper.selectDetailArticle(map);
        return list;
    }

    @Cacheable(value = "myarticle",key="'directive_limit_'+#paramMap['limit'].toString()+'_channelId_'+#paramMap['channelId'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogArticle> selectArticleData(Map<String, Object> paramMap) {
        Long channelId = (Long)paramMap.get("channelId");
        Integer limit = (Integer)paramMap.get("limit");
        EntityWrapper<BlogArticle> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",channelId);
        wrapper.orderBy("is_top",false).orderBy("is_recommend",false)
                .orderBy("sort",false).orderBy("publist_time",false);
        Page<BlogArticle> pageData = selectPage(new Page<BlogArticle>(1,limit),wrapper);
        return pageData.getRecords();
    }

    @Cacheable(value = "myarticle",key="'directive_index_limit_'+#paramMap['limit'].toString()+'_order_'+#paramMap['order'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogArticle> selectBlogIndexArticles(Map<String, Object> paramMap) {
        String order = (String)paramMap.get("order");
        String[] list = order.split(",");
        List<String> orderString = Lists.newArrayList();
        for(int i=0;i<list.length;i++){
            if(order.contains(list[i])){
                if("top".equals(list[i])){
                    list[i] = "is_top";
                }
                if("recommend".equals(list[i])){
                    list[i] = "is_recommend";
                }
                if("publish".equals(list[i])){
                    list[i] = "publist_time";
                }
                if("view".equals(list[i])){
                    list[i] = "click";
                }
                orderString.add(list[i]);
            }else {
                throw new MyException("模版传参错误");
            }
        }
        if(orderString.size()>0){
            paramMap.put("orderList",orderString);
        }
        paramMap.remove("order");
        return baseMapper.selectIndexArticle(paramMap);
    }

    @Caching(evict = {
            @CacheEvict(value = "myarticle",allEntries = true),
            @CacheEvict(value = "blogTagsData",allEntries = true),
            @CacheEvict(value = "oneArticle",allEntries = true),
    })
    @Override
    public BlogArticle saveOrUpdateArticle(BlogArticle blogArticle) {
        insertOrUpdate(blogArticle);
        return blogArticle;
    }

    @Override
    public void saveArticleTags(Map<String, Object> map) {
        baseMapper.saveArticleTags(map);
    }

    @Override
    public void removeArticleTags(Long articleId) {
        baseMapper.removeArticleTags(articleId);
    }

    @Override
    public Integer getArticleClick(Long articleId) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Integer count = (Integer)operations.get("article_click_id_"+articleId);
        if(count == null){
            BlogArticle blogArticle = selectById(articleId);
            if(blogArticle.getClick() != null){
                count = blogArticle.getClick();
            }else{
                count = 0;
            }
        }
        return count == null? 0 : count;
    }

    @CachePut(value = "showBlog",key = "'article_click_id_'+#articleId",unless = "#result == null")
    @Override
    public Integer flashArticleClick(Long articleId) {
        return getArticleClick(articleId)+1;
    }

    @Override
    public void createArticlIndex() {
        File fileDectory = new File(LuceneSearch.dir);
        if(!fileDectory.exists()){
            fileDectory.mkdir();
        }else {
            File[] f = fileDectory.listFiles();
            if(f.length>0){
                for (File file : f){
                    file.delete();
                }
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("isBaseChannel",true);
        List<BlogArticle> list = selectDetailArticle(map);
        for (BlogArticle blogArticle : list){
            Document doc = new Document();
            doc.add(new LongPoint("id",blogArticle.getId()));
            doc.add(new TextField("title",blogArticle.getTitle(), Field.Store.YES));
            doc.add(new TextField("marks",blogArticle.getMarks()==null?"":blogArticle.getMarks(),Field.Store.YES));
            doc.add(new TextField("text",blogArticle.getText()==null?"":blogArticle.getText(),Field.Store.YES));
            doc.add(new StoredField("href",blogArticle.getBlogChannel().getHref()));
            doc.add(new StoredField("show_pic",blogArticle.getShowPic()==null?"":blogArticle.getShowPic()));
            doc.add(new StoredField("id",blogArticle.getId()));
            try {
                LuceneSearch.write(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Cacheable(value = "myarticle",key="'time_line_channel_id_'+#id.toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogArticle> selectTimeLineList(Long id) {
        EntityWrapper<BlogArticle> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",id);
        wrapper.orderBy("create_date",false);
        return selectList(wrapper);
    }


    @Override
    public List<BlogArticle> selectNewCommentArticle(Integer limit) {
        return baseMapper.selectNewCommentArticle(limit);
    }

    @Cacheable(value = "blogTagsData",key = "'tag_'+#map['articleId'].toString()+'_sameArticles_limit_'+#map['limit'].toString()")
    @Override
    public List<BlogArticle> selectLikeSameWithTags(Map<String, Object> map) {
        return baseMapper.selectLikeSameWithTags(map);
    }
}
