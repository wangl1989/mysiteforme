/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 11:54:07
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 23:45:58
 * @ Description: Redis基础操作类
 */

package com.mysiteforme.admin.redis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.MessageConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String userTips = MessageConstants.Exception.EXCEPTION_REDIS_DATA;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ============================== 基础操作 ==============================

    /**
     * 获取所有匹配模式的键
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Redis数据异常:获取所有匹配模式的键异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     *  获取键类型
     */
    public DataType type(String key) {
        try {
            return redisTemplate.type(key);
        } catch (Exception e) {
            log.error("Redis数据异常:获取键类型异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 指定缓存失效时间
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:设置过期时间异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis数据异常:判断key存在异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 删除缓存
     */
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(Arrays.asList(keys));
            }
        }
    }

    // ============================== String操作 ==============================

    /**
     * 普通缓存获取
     */
    public <T> T get(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        
        try {
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            }
            log.warn("Redis数据异常:类型转换失败：期望{}，实际是{}", clazz.getName(), obj.getClass().getName());
            return null;
        } catch (Exception e) {
            log.error("Redis数据异常:Redis数据转换异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 普通缓存放入
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:设置缓存异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 普通缓存放入并设置时间
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:设置缓存异常", e);
            throw MyException.builder().systemError(userTips).throwable(e).build();
        }
    }

    /**
     * 递增
     */
    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 递增
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            log.error("Redis数据异常:递增因子必须大于0");
            throw MyException.builder().systemError(userTips).build();
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            log.error("Redis数据异常:递减因子必须大于0");
            throw MyException.builder().systemError(userTips).build();
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================== Hash操作 ==============================

    /**
     * HashGet
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     */
    public boolean hmset(String key, String mapKey, Object mapVlue) {
        try {
            redisTemplate.opsForHash().put(key, mapKey, mapVlue);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:Hash设置异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * HashSet
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:Hash设置异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    // ============================== List操作 ==============================

    public Long getListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("Redis数据异常:获取list长度出现异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    public void leftPushList(String key, Object value){
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Redis数据异常:设置list左边数据异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    public void rightPushListAll(String key,Object... objects){
        try {
            redisTemplate.opsForList().rightPushAll(key, objects);
        } catch (Exception e) {
            log.error("Redis数据异常:设置list全部数据异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 获取list缓存的内容
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis数据异常:获取list异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 将list放入缓存
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:设置list异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 将list右边的最后一个数据移除
     */
    public boolean removeRightList(String key) {
        try {
            redisTemplate.opsForList().rightPop(key);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:移除右边的list数据异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    // ============================== Set操作 ==============================

    public Long getSetSize(String key){
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 根据key获取Set中的所有值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis数据异常:获取set异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    public boolean sSet(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
            redisTemplate.expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:设置set异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 将数据从set缓存中移除
     */
    public boolean sRemove(String key, Object... values) {
        try {
            redisTemplate.opsForSet().remove(key, values);
            return true;
        } catch (Exception e) {
            log.error("Redis数据异常:移除set异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    // ============================== ZSet操作 ==============================

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            log.error("Redis数据异常:添加ZSet异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 获取zet元素
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis数据异常:获取ZSet异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    /**
     * 移除Zset元素
     */
    public boolean zRemove(String key, Object value) {
        try {
            redisTemplate.opsForZSet().remove(key, value);
            return false;
        } catch (Exception e) {
            log.error("Redis数据异常:移除ZSet异常", e);
            throw MyException.builder().systemError(userTips).build();
        }
    }

    // ============================== 分布式锁 ==============================

    /**
     * 获取分布式锁
     */
    public boolean lock(String lockKey, String value, long expireTime, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, value, expireTime, timeUnit));
    }

    /**
     * 释放分布式锁
     */
    public boolean unlock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        return Long.valueOf(1L).equals(result);
    }

    // ============================== 限流操作 ==============================

    /**
     * 简单限流
     */
    public boolean isActionAllowed(String key, int limit, int timeout, TimeUnit timeUnit) {
        try {
            Long count = redisTemplate.opsForValue().increment(key, 1);
            if (count == 1) {
                redisTemplate.expire(key, timeout, timeUnit);
            }
            return count <= limit;
        } catch (Exception e) {
            log.error("Redis数据异常:限流异常", e);
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("消息请求太快了，请稍后再试").build();
        }
    }

    /**
     * 检查token是否在黑名单中
     * @param token token值
     * @return true：在黑名单中，false：不在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("token:blacklist:" + token);
    }
    
    /**
     * 将token加入黑名单（比如在用户登出时调用）
     * @param token token值
     * @param timeToLive token的有效期，单位：毫秒
     */
    public void addToBlacklist(String token, long timeToLive) {
        redisTemplate.opsForValue().set(
            "token:blacklist:" + token,
            "1",
            timeToLive,
            TimeUnit.MILLISECONDS
        );
    }

}

