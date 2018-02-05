package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogComment;
import com.mysiteforme.admin.dao.BlogCommentDao;
import com.mysiteforme.admin.service.BlogCommentService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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
        Object o = selectObj(Condition.create().setSqlSelect("max(floor)").eq("article_id",articleId));
        Integer floor = 0;
        if(o != null){
            floor =  (Integer)o;
        }
        return floor;
    }

    @Override
    public Page<BlogComment> getArticleComments(Long articleId,Page<BlogComment> page) {
        EntityWrapper<BlogComment> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("article_id",articleId);
        wrapper.orderBy("floor",true);
        return selectPage(page,wrapper);
    }
}
