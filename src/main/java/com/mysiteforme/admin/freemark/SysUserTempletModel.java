package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.service.UserCacheService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangl on 2018/1/23.
 * todo: 根据用户ID获取用户详情
 */
@Component
public class SysUserTempletModel implements TemplateMethodModelEx {
    private UserCacheService userCacheService;

    public SysUserTempletModel(){

    }

    @Autowired
    public SysUserTempletModel(UserCacheService userCacheService){
        this.userCacheService = userCacheService;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object exec(List list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("参数为空");
        }
        SimpleNumber simpleNumber = (SimpleNumber) list.get(0);
        if(simpleNumber == null){
            return null;
        }
        Long userId = simpleNumber.getAsNumber().longValue();
        return userCacheService.findUserById(userId);
    }
}
