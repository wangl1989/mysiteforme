package com.mysiteforme.admin.freemark;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.BlogArticleService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2018/2/7.
 * todo:
 */
@Component
public class LookLikeArticlesTempletModel extends BaseDirective implements TemplateDirectiveModel {

    private BlogArticleService blogArticleService;

    public LookLikeArticlesTempletModel() {
        super();
    }

    @Autowired
    public LookLikeArticlesTempletModel(BlogArticleService blogArticleService) {
        this.blogArticleService = blogArticleService;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        int limit = 5;
        Long articleId = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("articleid")){
                articleId = getLong(paramValue);
            }
            if(paramName.equalsIgnoreCase("limit")){
                int templimit = getInt(paramValue);
                if(templimit>0){
                    limit = templimit;
                }
            }
        }
        if(articleId == null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("模版参数错误:文章ID未找到").build();
        }
        Map<String,Object> dataMap = Maps.newHashMap();
        dataMap.put("limit",limit);
        dataMap.put("articleId",articleId);
        List<BlogArticle> articleList = blogArticleService.selectLikeSameWithTags(dataMap);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(articleList));
        templateDirectiveBody.render(environment.getOut());
    }
}
