package com.mysiteforme.admin.redis;

import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.base.MySecurityUser;

/**
 * Created by wangl on 2018/1/20.
 * todo:
 */
@Component
public class CacheUtils {

    private UserDao userDao;

    public CacheUtils() {}

    @Autowired
    public CacheUtils(UserDao userDao) {
        this.userDao = userDao;
    }



    /**
     * 清除当前用户redis缓存
     */
    @Caching(evict = {
            @CacheEvict(value = "user", key = "'user_id_'+T(String).valueOf(#result.id)",condition = "#result.id != null and #result.id != 0"),
            @CacheEvict(value = "user", key = "'user_name_'+#result.loginName", condition = "#result.loginName !=null and #result.loginName != ''"),
            @CacheEvict(value = "user", key = "'user_email_'+#result.email", condition = "#result.email != null and #result.email != ''"),
            @CacheEvict(value = "user", key = "'user_tel_'+#result.tel", condition = "#result.tel != null and #result.tel != ''" ),
    })
    public User clearUserCache(){
        return userDao.selectById(MySecurityUser.id());
    }
}
