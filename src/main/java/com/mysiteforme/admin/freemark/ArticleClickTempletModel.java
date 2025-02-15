/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:00:41
 * @ Description: 文章点击量标签 已废弃
 */

package com.mysiteforme.admin.freemark;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.mysiteforme.admin.service.BlogArticleService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;

@Deprecated
public class ArticleClickTempletModel implements TemplateMethodModelEx {


    private BlogArticleService blogArticleService;
    public ArticleClickTempletModel(){}

    @Autowired
    public ArticleClickTempletModel(BlogArticleService blogArticleService){
        this.blogArticleService = blogArticleService;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object exec(List list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("参数为空");
        }
        SimpleNumber simpleNumber = (SimpleNumber) list.get(0);
        Long articleId = simpleNumber.getAsNumber().longValue();

        return blogArticleService.getArticleClick(articleId);
    }
}
