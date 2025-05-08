/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 11:27:04
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:26:32
 * @ Description: Redis配置类
 */

package com.mysiteforme.admin.redis;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * @param connectionFactory 连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key值【专门用于String类型的序列化，是Redis官方默认的序列化方式】
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setStringSerializer(stringSerializer);

        // 使用Jackson2JsonRedisSerializer作为序列化器【不保存类型信息，序列化后的数据更紧凑】
        // Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        
        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值【会在序列化时保存类型信息，更适合复杂对象】
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 是 Spring 的 InitializingBean 接口中定义的方法，它的主要作用是在 bean 的所有属性被设置完成后，执行自定义的初始化逻辑。
        // 1. 检查必要配置：验证 connectionFactory 是否已经设置；如果没有设置，会抛出 IllegalStateException
        // 2. 初始化默认序列化器：如果没有手动设置各种序列化器，会初始化默认的序列化器；默认使用 JdkSerializationRedisSerializer，但这不是最优选择
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器
     * @param redisConnectionFactory Redis连接工厂
     * @return RedisCacheManager实例
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
            .entryTtl(Duration.ofHours(1)); // 设置默认过期时间

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build();
    }
}

