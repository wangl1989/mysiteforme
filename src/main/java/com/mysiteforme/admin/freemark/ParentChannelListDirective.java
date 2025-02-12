package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.entity.BlogChannel;
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
 * Created by wangl on 2018/1/22.
 * todo: 当前栏目(文章)父栏目集合
 */
@Component
public class ParentChannelListDirective extends BaseDirective implements TemplateDirectiveModel {

    private BlogChannelService blogChannelService;

    public ParentChannelListDirective() {
        super();
    }

    @Autowired
    public ParentChannelListDirective(BlogChannelService blogChannelService) {
        this.blogChannelService = blogChannelService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        Long cid = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("cid")){
                cid = getLong(paramValue);
            }
        }
        if(cid == null){
            throw new TemplateModelException("文章所属栏目ID不能为空");
        }
        List<BlogChannel> list  = blogChannelService.getParentsChannel(cid);
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(list));
        templateDirectiveBody.render(environment.getOut());
    }
}
