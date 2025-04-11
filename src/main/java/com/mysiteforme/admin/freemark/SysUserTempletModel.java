/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:01:09
 * @ Description: 根据用户ID获取用户详情 已废弃
 */

package com.mysiteforme.admin.freemark;

import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserService;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Deprecated
public class SysUserTempletModel implements TemplateMethodModelEx {
    private UserService userService;

    public SysUserTempletModel(){

    }

    public SysUserTempletModel(UserCacheService userCacheService){
        this.userService = userService;
    }

    @Override
    public Object exec(List list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("参数为空");
        }
        SimpleNumber simpleNumber = (SimpleNumber) list.get(0);
        if(simpleNumber == null){
            return null;
        }
        Long userId = simpleNumber.getAsNumber().longValue();
        return userService.getById(userId);
    }
}
