package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.service.UserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 在 Spring 中，@Cacheable 注解在同一个类中自调用时不会生效，
 * 因为 Spring AOP 代理无法拦截同一个类中的方法调用。
 * 为了解决这个问题，可以将需要缓存的方法提取到一个单独的服务类中，
 * 然后在原来的服务类中注入这个新服务类并调用其方法。
 */
@Service("userCacheService")
public class UserCacheServiceImpl implements UserCacheService {

    private UserDao baseMapper;

    public UserCacheServiceImpl() {}

    @Autowired
    public UserCacheServiceImpl(UserDao baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * 根据用户ID查找用户信息
     * 结果会被缓存，缓存key为'user_id_'+用户ID
     * 当结果为null时不缓存
     * @param id 用户ID
     * @return 用户信息对象，未找到时返回null
     */
    @Cacheable(value = "user",key="'user_id_'+T(String).valueOf(#id)",unless = "#result == null")
    public User findUserById(Long id) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("id", id);
        return baseMapper.selectUserByMap(map);
    }
}
