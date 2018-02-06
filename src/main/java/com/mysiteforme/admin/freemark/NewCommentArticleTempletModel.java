package com.mysiteforme.admin.freemark;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.service.BlogArticleService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class NewCommentArticleTempletModel extends BaseDirective implements TemplateDirectiveModel {

    @Autowired
    private BlogArticleService blogArticleService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator iterator = map.entrySet().iterator();
        Integer limit = 5;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = (Map.Entry<String, TemplateModel>) iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.toLowerCase().equals("limit")){
                int templimit = getInt(paramName,paramValue);
                if(templimit>0){
                    limit = templimit;
                }
            }
        }
        List<BlogArticle> articleList = blogArticleService.selectNewCommentArticle(limit);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(articleList));
        templateDirectiveBody.render(environment.getOut());
    }
}
