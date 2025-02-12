package com.mysiteforme.admin.freemark;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    private BlogChannelService blogChannelService;

    public ChannelDirective(){
        super();
    }

    @Autowired
    public ChannelDirective(BlogChannelService blogChannelService){
        this.blogChannelService = blogChannelService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        QueryWrapper<BlogChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        int limit = 10;
        Long pid = null;
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("limit")){
                limit = getInt(paramValue);
            }
            if(paramName.equalsIgnoreCase("pid")){
                pid = getLong(paramValue);
            }
        }
        if(pid == null){
            wrapper.isNull("parent_id");
        }else{
            wrapper.eq("parent_id",pid);
        }
        wrapper.orderBy(false,false,"sort");
        List<BlogChannel> channelList = blogChannelService.getChannelListByWrapper(limit,wrapper);
        if(channelList.isEmpty()){
            throw new TemplateModelException("返回值为空");
        }
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(channelList));
        templateDirectiveBody.render(environment.getOut());

    }
}
