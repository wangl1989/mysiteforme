package com.mysiteforme.admin.redis;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by wangl on 2017/11/25.
 * todo:
 */
@Setter
@Getter
@Component
public class RedisCacheManager implements CacheManager {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new ShiroCache<>(s, redisTemplate);
    }

}
