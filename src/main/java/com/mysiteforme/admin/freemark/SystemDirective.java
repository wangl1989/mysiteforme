/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:01:15
 * @ Description: 获取字典标签 已废弃
 */

package com.mysiteforme.admin.freemark;

import com.google.common.collect.Lists;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.service.DictService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Deprecated
public class SystemDirective extends BaseDirective implements TemplateDirectiveModel {

    private DictService dictService;

    public SystemDirective() {
        super();
    }

    @Autowired
    public SystemDirective(DictService dictService) {
        this.dictService = dictService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Iterator<Map.Entry<String, TemplateModel>> iterator = map.entrySet().iterator();
        List<Dict> dictList = Lists.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, TemplateModel> param = iterator.next();
            String paramName = param.getKey();
            TemplateModel paramValue = param.getValue();
            if(paramName.equalsIgnoreCase("type")){
                String dicType = getString(paramValue);
                if(StringUtils.isBlank(dicType)){
                    throw new TemplateModelException("参数名称不正确");
                }else{
                    dictList = dictService.getDictByType(dicType);
                }
            }

        }
        if(dictList.isEmpty()){
            throw new TemplateModelException("返回值为空");
        }
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        environment.setVariable("result", builder.build().wrap(dictList));
        templateDirectiveBody.render(environment.getOut());
    }


}
