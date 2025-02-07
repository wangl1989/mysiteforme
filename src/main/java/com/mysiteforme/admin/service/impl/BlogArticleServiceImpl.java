package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.dao.BlogArticleDao;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.lucene.LuceneSearch;
import com.mysiteforme.admin.service.BlogArticleService;
import com.mysiteforme.admin.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.lucene.document.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


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

    private static final Logger logger = LoggerFactory.getLogger(BlogArticleServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public BlogArticleServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /***
     * 根据栏目ID清除文章与栏目的关系
     * @param channelId 栏目ID
     */
    @Override
    public void removeArticleChannel(Long channelId) {
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("channel_id",channelId);
        List<BlogArticle> list = list(wrapper);
        if(!list.isEmpty()){
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
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    @Override
    public IPage<BlogArticle> selectDetailArticle(Map<String, Object> map, IPage<BlogArticle> page) {
        List<BlogArticle> list = baseMapper.selectDetailArticle(map,page);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<BlogArticle> selectDetailArticle(Map<String, Object> map) {
        return baseMapper.selectDetailArticle(map);
    }

    @Cacheable(value = "myarticle",key="'directive_limit_'+#paramMap['limit'].toString()+'_channelId_'+#paramMap['channelId'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogArticle> selectArticleData(Map<String, Object> paramMap) {
        Long channelId = (Long)paramMap.get("channelId");
        Integer limit = (Integer)paramMap.get("limit");
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",channelId);
        wrapper.orderBy(false,false,"is_top","is_recommend","sort","publist_time");
        IPage<BlogArticle> pageData = page(new Page<>(1,limit),wrapper);
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
        if(!orderString.isEmpty()){
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
    public void saveOrUpdateArticle(BlogArticle blogArticle) {
        saveOrUpdate(blogArticle);
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
            BlogArticle blogArticle = getById(articleId);
            if(blogArticle.getClick() != null){
                count = blogArticle.getClick();
            }else{
                count = 0;
            }
        }
        return count;
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
            if(!fileDectory.mkdir()){
                throw new MyException("创建索引文件夹失败:"+fileDectory.getName());
            }
        }else {
            File[] f = fileDectory.listFiles();
            if (f != null) {
                for (File file : f) {
                    if(!file.delete()){
                        throw new MyException("删除索引文件失败:"+file.getName());
                    }
                }
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("isBaseChannel",true);
        List<BlogArticle> list = selectDetailArticle(map);
        for (BlogArticle blogArticle : list){
            Document doc = DocumentUtil.writeDoc(blogArticle);
            doc.add(new StoredField("id",blogArticle.getId()));
            try {
                LuceneSearch.write(doc);
            } catch (IOException e) {
                logger.error("创建文章索引失败",e);
                throw new MyException("创建文章索引失败");
            }
        }
    }



    @Cacheable(value = "myarticle",key="'time_line_channel_id_'+#id.toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<BlogArticle> selectTimeLineList(Long id) {
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("channel_id",id);
        wrapper.orderBy(false,false,"create_date");
        return list(wrapper);
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
