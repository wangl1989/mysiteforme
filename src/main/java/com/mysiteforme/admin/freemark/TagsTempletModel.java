package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.entity.BlogTags;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.BlogTagsService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2018/2/4.
 * todo: 获取文章的标签集合
 */
@Component
public class TagsTempletModel extends BaseDirective implements TemplateDirectiveModel {

    @Autowired
    private BlogTagsService blogTagsService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator iterator = map.entrySet().iterator();
        Long aid = null;
        Long cid = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = (Map.Entry<String, TemplateModel>) iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.toLowerCase().equals("aid")){
                aid = getLong(paramName,paramValue);
            }
            if(paramName.toLowerCase().equals("cid")){
                cid = getLong(paramName,paramValue);
            }
        }
        if(aid != null && cid != null){
            throw new MyException("文章ID跟栏目ID不能同时存在");
        }
        List<BlogTags> list  = null;
        if(aid != null){
            list = blogTagsService.getTagsByArticleId(aid);
        }
        if(cid != null){
            list = blogTagsService.getTagsByChannelId(cid);
        }
        if(cid == null || aid == null){
            list = blogTagsService.getTagsByChannelId(null);
        }
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(list));
        templateDirectiveBody.render(environment.getOut());
    }
}
