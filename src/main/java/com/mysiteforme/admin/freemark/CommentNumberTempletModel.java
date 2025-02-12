package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.BlogCommentService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
