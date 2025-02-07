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

    /**
     * 获取指定文章或留言板的最大楼层数
     * @param articleId 文章ID，为null时表示获取留言板的最大楼层
     * @return 最大楼层数，无评论时返回0
     */
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

    /**
     * 获取指定回复评论下的最大楼层数
     * @param replyId 被回复的评论ID
     * @return 最大楼层数，无回复时返回0
     */
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

    /**
     * 分页获取文章评论列表
     * @param articleId 文章ID
     * @param type 评论类型
     * @param page 分页参数
     * @return 分页的评论列表
     */
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

    /**
     * 保存或更新评论
     * 同时清除文章评论数量的缓存
     * @param blogComment 评论对象
     */
    @CacheEvict(value = "commentData",key = "'article_'+#blogComment.articleId+'_commentcount'")
    @Override
    public void saveOrUpdateBlogComment(BlogComment blogComment) {
        saveOrUpdate(blogComment);
    }

    /**
     * 获取文章的评论数量
     * 结果会被缓存
     * @param articleId 文章ID
     * @return 评论数量
     */
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
