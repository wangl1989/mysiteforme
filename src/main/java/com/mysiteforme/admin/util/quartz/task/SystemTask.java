/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:32:31
 * @ Description: 系统定时任务（可在前端配置）
 */

package com.mysiteforme.admin.util.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.service.BlogArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("systemTask")
public class SystemTask {

    private final RedisTemplate<String, Object> redisTemplate;

    private final BlogArticleService blogArticleService;

    @Autowired
    public SystemTask(RedisTemplate<String, Object> redisTemplate, BlogArticleService blogArticleService) {
        this.redisTemplate = redisTemplate;
        this.blogArticleService = blogArticleService;
    }

    /**
     * 同步文章点击量
     */
    public void  synchronizationArticleView(String params){
        ValueOperations<String, Object> operations=redisTemplate.opsForValue();
        QueryWrapper<BlogArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        List<BlogArticle> list = blogArticleService.list(wrapper);
        for (BlogArticle article : list){
            String key = "article_click_id_"+article.getId();
            if(redisTemplate.hasKey(key)){
                Integer count = (Integer)operations.get(key);
                if(count != null && count > 0){
                    article.setClick(blogArticleService.getArticleClick(article.getId()));
                    if(StringUtils.isNotBlank(params)){
                        article.setUpdateId(Long.valueOf(params));
                    }
                    blogArticleService.updateById(article);
                }
            }
        }
    }

    /**
     * 生成文章搜索索引
     */
    public void createArticleIndex(String params) {
        blogArticleService.createArticlIndex();
    }

}
