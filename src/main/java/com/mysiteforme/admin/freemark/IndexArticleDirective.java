package com.mysiteforme.admin.freemark;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.BlogArticle;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.BlogArticleService;
import com.mysiteforme.admin.service.BlogChannelService;
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
 * todo:获取文章列表数据（可选参数：排序，集合长度）
 */
@Component
public class IndexArticleDirective extends BaseDirective implements TemplateDirectiveModel {

    @Autowired
    private BlogArticleService blogArticleService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator iterator = map.entrySet().iterator();
        Map<String,Object> paramMap = Maps.newHashMap();
        Integer limit = 10;
        String order = "top,recommend,publish,sort";
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = (Map.Entry<String, TemplateModel>) iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.toLowerCase().equals("limit")){
                limit = getInt(paramName,paramValue);
            }
            if(paramName.toLowerCase().equals("order")){
                order = getString(paramName,paramValue);
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
