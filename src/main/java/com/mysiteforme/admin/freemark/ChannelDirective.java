package com.mysiteforme.admin.freemark;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
 * Created by wangl on 2018/1/20.
 * todo: 获取指定父栏目下的子栏目集合
 */
@Component
public class ChannelDirective extends BaseDirective implements TemplateDirectiveModel {

    @Autowired
    private BlogChannelService blogChannelService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator iterator = map.entrySet().iterator();
        EntityWrapper<BlogChannel> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        Integer limit = 10;
        Long pid = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = (Map.Entry<String, TemplateModel>) iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.toLowerCase().equals("limit")){
                limit = getInt(paramName,paramValue);
            }
            if(paramName.toLowerCase().equals("pid")){
                pid = getLong(paramName,paramValue);
            }
        }
        if(pid == null){
            wrapper.isNull("parent_id");
        }else{
            wrapper.eq("parent_id",pid);
        }
        wrapper.orderBy("sort",false);
        List<BlogChannel> channelList = blogChannelService.getChannelListByWrapper(limit,wrapper);
        if(channelList.size()<=0){
            throw new TemplateModelException("返回值为空");
        }
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(channelList));
        templateDirectiveBody.render(environment.getOut());

    }
}
