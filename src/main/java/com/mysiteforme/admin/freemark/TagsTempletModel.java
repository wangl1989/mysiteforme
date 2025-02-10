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
 * Created by wang on 2018/2/4.
 * todo: 获取文章的标签集合
 */
@Component
public class TagsTempletModel extends BaseDirective implements TemplateDirectiveModel {

    private BlogTagsService blogTagsService;

    public TagsTempletModel() {
        super();
    }

    @Autowired
    public TagsTempletModel(BlogTagsService blogTagsService) {
        this.blogTagsService = blogTagsService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        Long aid = null;
        Long cid = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param =  iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("aid")){
                aid = getLong(paramValue);
            }
            if(paramName.equalsIgnoreCase("cid")){
                cid = getLong(paramValue);
            }
        }
        if(aid != null && cid != null){
            throw MyException.builder().code(MyException.VALIDATION_ERROR).msg("文章ID跟栏目ID不能同时存在").build();
        }
        List<BlogTags> list  = null;
        if(aid != null){
            list = blogTagsService.getTagsByArticleId(aid);
        }
        if(cid != null){
            list = blogTagsService.getTagsByChannelId(cid);
        }
        if(cid == null && aid == null){
            list = blogTagsService.getTagsByChannelId(null);
        }
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(list));
        templateDirectiveBody.render(environment.getOut());
    }
}
