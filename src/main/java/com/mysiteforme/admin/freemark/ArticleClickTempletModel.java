package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.service.BlogArticleService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangl on 2018/1/22.
 * todo:文章点击量标签
 */
@Component
public class ArticleClickTempletModel implements TemplateMethodModelEx {


    private BlogArticleService blogArticleService;
    public ArticleClickTempletModel(){}

    @Autowired
    public ArticleClickTempletModel(BlogArticleService blogArticleService){
        this.blogArticleService = blogArticleService;
    }

    @Override
    public Object exec(List list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("参数为空");
        }
        SimpleNumber simpleNumber = (SimpleNumber) list.get(0);
        Long articleId = simpleNumber.getAsNumber().longValue();

        return blogArticleService.getArticleClick(articleId);
    }
}
