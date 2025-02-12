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

/**
 * Created by wangl on 2018/1/20.
 * todo: 获取指定栏目下的文章集合
 */
@Component
public class ArticleDirective extends BaseDirective implements TemplateDirectiveModel {


    private BlogArticleService blogArticleService;

    public ArticleDirective() {
        super();
    }

    @Autowired
    public ArticleDirective(BlogArticleService blogArticleService) {
        this.blogArticleService = blogArticleService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        Map<String,Object> paramMap = Maps.newHashMap();
        int limit = 5;
        Long channelId = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("limit")){
                limit = getInt(paramValue);
            }
            if(paramName.equalsIgnoreCase("channelid")){
                channelId = getLong(paramValue);
            }
        }
        if(channelId == null){
            throw new TemplateModelException("文章所属栏目ID不能为空");
        }
        paramMap.put("limit",limit);
        paramMap.put("channelId",channelId);
        List<BlogArticle> articleList = blogArticleService.selectArticleData(paramMap);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(articleList));
        templateDirectiveBody.render(environment.getOut());
    }
}
