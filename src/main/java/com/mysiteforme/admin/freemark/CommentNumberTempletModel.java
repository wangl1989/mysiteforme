/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:56:58
 * @ Description: 评论数量标签
 */

package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.BlogCommentService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Deprecated
public class CommentNumberTempletModel implements TemplateMethodModelEx {

    private BlogCommentService blogCommentService;

    public CommentNumberTempletModel(){}

    @Autowired
    public CommentNumberTempletModel(BlogCommentService blogCommentService){
        this.blogCommentService = blogCommentService;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public Object exec(List list) {
        if(list == null || list.isEmpty()){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("模板参数为空").build();
        }
        SimpleNumber simpleNumber = (SimpleNumber) list.get(0);
        Long articleId = simpleNumber.getAsNumber().longValue();
        return blogCommentService.getArticleCommentsCount(articleId);
    }
}
