package com.mysiteforme.admin.freemark;

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

    private BlogArticleService blogArticleService;

    public NewCommentArticleTempletModel() {
        super();
    }

    @Autowired
    public NewCommentArticleTempletModel(BlogArticleService blogArticleService) {
        this.blogArticleService = blogArticleService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        int limit = 5;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("limit")){
                int implicit = getInt(paramValue);
                if(implicit>0){
                    limit = implicit;
                }
            }
        }
        List<BlogArticle> articleList = blogArticleService.selectNewCommentArticle(limit);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(articleList));
        templateDirectiveBody.render(environment.getOut());
    }
}
