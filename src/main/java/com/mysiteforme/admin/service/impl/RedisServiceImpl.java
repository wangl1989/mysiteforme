package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.request.PageListRedisRequest;
import com.mysiteforme.admin.entity.response.RedisDataResponse;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RedisService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public IPage<RedisDataResponse> getRedisDataPageList(PageListRedisRequest request) {
        // 如果未指定模式，则默认匹配所有键
        String pattern = StringUtils.isBlank(request.getKeyPattern()) ? "*" : request.getKeyPattern();

        // 获取所有匹配模式的键
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) {
            return new Page<>(request.getPage(), request.getLimit());
        }

        // 转换为列表以进行分页
        List<String> keyList = new ArrayList<>(keys);
        keyList = keyList.stream().filter(s -> !s.startsWith("auth:access") && !s.startsWith("auth:refresh") && !s.startsWith("auth:user:device")).collect(Collectors.toList());

        // 如果需要，按过期时间排序
        if (Boolean.TRUE.equals(request.getSortByExpireAsc())) {
            keyList.sort(Comparator.comparing(key -> redisTemplate.getExpire(key, TimeUnit.SECONDS)));
        }

        // 计算分页
        int total = keyList.size();
        int startIndex = (request.getPage() - 1) * request.getLimit();
        int endIndex = Math.min(startIndex + request.getLimit(), total);

        // 防止索引越界
        if (startIndex >= total) {
            startIndex = 0;
            endIndex = 0;
        }

        // 获取当前页的键子集
        List<String> pageKeys = keyList.subList(startIndex, endIndex);

        // 获取每个键的数据
        List<RedisDataResponse> redisDataList = pageKeys.stream()
                .map(this::getRedisData)
                .collect(Collectors.toList());

        // 创建分页对象
        IPage<RedisDataResponse> pageData = new Page<>(request.getPage(), request.getLimit(), total);
        pageData.setRecords(redisDataList);
        return pageData;
    }

    @Override
    public void deleteRedisData(String key) {
        if (StringUtils.isBlank(key)) {
            throw MyException.builder().paramMsgError(MessageConstants.Redis.REDIS_KEY_CAN_NOT_EMPTY).build();
        }

        if (!redisTemplate.hasKey(key)) {
            throw MyException.builder().paramMsgError(MessageConstants.Redis.REDIS_KEY_NOT_FOUND).build();
        }

        redisTemplate.delete(key);
    }

    /**
     * 获取特定键的Redis数据
     * @param key Redis键
     * @return 包含键、类型、过期时间和值的Redis数据响应
     */
    private RedisDataResponse getRedisData(String key) {
        try {
            // 获取键类型
            DataType dataType = redisTemplate.type(key);
            String type = dataType.name().toLowerCase();

            // 获取过期时间
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            // 根据类型获取值
            String value = getValueAsString(key, type);

            return RedisDataResponse.builder()
                    .key(key)
                    .type(type)
                    .ttl(ttl)
                    .value(value)
                    .build();
        } catch (Exception e) {
            log.error("获取Redis键的数据时出错: {}", key, e);
            throw MyException.builder().businessError(MessageConstants.Redis.REDIS_GET_VALUE_EXCEPTION,e.getMessage()).build();
        }
    }

    /**
     * 根据数据类型获取Redis值的字符串表示
     * @param key Redis键
     * @param type Redis数据类型
     * @return 值的字符串表示
     */
    private String getValueAsString(String key, String type) {
        try {
            return switch (type.toLowerCase()) {
                case "string" -> {
                    // 直接使用StringRedisTemplate获取字符串值
                    String value = redisTemplate.opsForValue().get(key);
                    yield value != null ? value : "空值";
                }
                case "list" -> {
                    // 直接使用低级API获取列表元素
                    List<String> list = redisTemplate.opsForList().range(key, 0, 9);
                    yield list == null || list.isEmpty() ? "[]" : list.toString();
                }
                case "hash" -> {
                    // 直接使用低级API获取哈希表条目
                    Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                    yield map.isEmpty() ? "{}" : formatMap(map);
                }
                case "set" -> {
                    // 直接使用低级API获取集合成员
                    try {
                        Set<String> set = redisTemplate.opsForSet().members(key);
                        yield set == null ? "[]" : set.toString();
                    } catch (Exception e) {
                        log.warn("获取Redis集合[{}]的值时出现异常: {}", key, e.getMessage());
                        yield "[无法读取的集合值]";
                    }
                }
                case "zset" -> {
                    // 直接使用低级API获取有序集合成员
                    Set<String> set =  redisTemplate.opsForZSet().range(key, 0, 9);
                    yield set == null ? "[]" : set.toString();
                }
                default -> throw MyException.builder().businessError(MessageConstants.Redis.REDIS_NOT_SUPPORT_TYPE,type).build();
            };
        } catch (Exception e) {
            log.error("获取键的值时出错: {}, 类型: {}", key, type, e);
            throw MyException.builder().businessError(MessageConstants.Redis.REDIS_GET_KEY_VALUE_ERROR,e.getMessage()).build();
        }
    }

    /**
     * 将映射格式化为字符串
     */
    private <K,V>String formatMap(Map<K, V> map) {
        if (map.size() > 10) {
            Map<K, V> truncated = new HashMap<>();
            int count = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (count >= 10) break;
                truncated.put(entry.getKey(), entry.getValue());
                count++;
            }
            return truncated + "... (已截断, 共" + map.size() + "个条目)";
        }
        return map.toString();
    }
}
