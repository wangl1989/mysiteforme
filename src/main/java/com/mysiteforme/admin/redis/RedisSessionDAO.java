package com.mysiteforme.admin.redis;

/*
  Created by wang on 2017/11/25.
  todo:redis实现共享session
 */
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    // session 在redis过期时间是30分钟30*60
    private static final int EXPIRE_TIME = 1800;

    private static final String PREFIX = "mysiteforme-shiro-session:";

    private RedisTemplate<String, Object> redisTemplate;

    public RedisSessionDAO() {
        super();
    }

    @Autowired
    public RedisSessionDAO(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 创建session，保存到数据库
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        logger.debug("创建session:{}", session.getId());
        redisTemplate.opsForValue().set(PREFIX + sessionId.toString(), session);
        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        logger.debug("获取session:{},当前的时间是：{}", sessionId, System.currentTimeMillis());
        // 先从缓存中获取session，如果没有再去数据库中获取
        Session session = super.doReadSession(sessionId);
        if (session == null) {
            session = (Session) redisTemplate.opsForValue().get(PREFIX + sessionId.toString());
        }
        return session;
    }

    // 更新session的最后一次访问时间
    @Override
    protected void doUpdate(Session session) {
        super.doUpdate(session);
        logger.debug("获取session:{}", session.getId());
        String key = PREFIX + session.getId().toString();
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, session);
        }
        redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    // 删除session
    @Override
    protected void doDelete(Session session) {
        logger.debug("删除session:{}", session.getId());
        super.doDelete(session);
        redisTemplate.delete(PREFIX + session.getId().toString());
    }
}
