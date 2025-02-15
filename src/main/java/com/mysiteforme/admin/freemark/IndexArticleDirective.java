/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:57:30
 * @ Description: 获取文章列表数据（可选参数：排序，集合长度） 已废弃
 */

package com.mysiteforme.admin.freemark;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.service.BlogArticleService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Deprecated
public class IndexArticleDirective extends BaseDirective implements TemplateDirectiveModel {

    private BlogArticleService blogArticleService;

    public IndexArticleDirective() {
        super();
    }

    @Autowired
    public IndexArticleDirective(BlogArticleService blogArticleService) {
        this.blogArticleService = blogArticleService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        Map<String,Object> paramMap = Maps.newHashMap();
        int limit = 10;
        String order = "top,recommend,publish,sort,view";
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("limit")){
                limit = getInt(paramValue);
            }
            if(paramName.equalsIgnoreCase("order")){
                order = getString(paramValue);
            }
        }
        paramMap.put("limit",limit);
        paramMap.put("order",order);
        List<BlogArticle> articleList = blogArticleService.selectBlogIndexArticles(paramMap);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(articleList));
        templateDirectiveBody.render(environment.getOut());
    }
}
