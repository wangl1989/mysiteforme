package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogComment;
import com.mysiteforme.admin.dao.BlogCommentDao;
import com.mysiteforme.admin.service.BlogCommentService;
import com.mysiteforme.admin.util.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 博客评论 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentDao, BlogComment> implements BlogCommentService {

    @Override
    public Integer getMaxFloor(Long articleId) {
        QueryWrapper<BlogComment> queryWrapper = new QueryWrapper<>();
        BlogComment comment;
        if(articleId != null){
            queryWrapper.eqSql("floor","select max(floor) from blog_comment ")
                        .eq("article_id",articleId)
                        .eq("del_flag",false);
        }else{
            queryWrapper.eqSql("floor","select max(floor) from blog_comment ")
                    .eq("type",Constants.COMMENT_TYPE_LEVING_A_MESSAGE)
                    .eq("del_flag",false);
        }
        comment = getOne(queryWrapper);

        Integer floor = 0;
        if(comment != null){
            floor =  comment.getFloor();
        }
        return floor;
    }

    @Override
    public Integer getMaxFloorByReply(Long replyId) {
        QueryWrapper<BlogComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eqSql("floor","select max(floor) from blog_comment ")
                    .eq("reply_id",replyId)
                    .eq("del_flag",false);
        BlogComment comment = getOne(queryWrapper);
        Integer floor = 0;
        if(comment != null){
            floor =  comment.getFloor();
        }
        return floor;
    }

    @Override
    public IPage<BlogComment> getArticleComments(Long articleId, Integer type, IPage<BlogComment> page) {
        Map<String,Object> map = Maps.newHashMap();
        if(articleId != null){
            map.put("articleId",articleId);
        }
        map.put("type",type);
        List<BlogComment> list = baseMapper.selectArticleCommentsByPlus(map,page);
        page.setRecords(list);
        return page;
    }

    @CacheEvict(value = "commentData",key = "'article_'+#blogComment.articleId+'_commentcount'")
    @Override
    public void saveOrUpdateBlogComment(BlogComment blogComment) {
        saveOrUpdate(blogComment);
    }

    @Cacheable(value = "commentData",key = "'article_'+#articleId+'_commentcount'")
    @Override
    public Integer getArticleCommentsCount(Long articleId) {
        QueryWrapper<BlogComment> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("type",Constants.COMMENT_TYPE_ARTICLE_COMMENT);
        wrapper.eq("article_id",articleId);
        return Math.toIntExact(count(wrapper));
    }
}
