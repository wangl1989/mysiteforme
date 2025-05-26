package com.mysiteforme.admin.config;

import com.mysiteforme.admin.entity.aiEntity.Additional;
import com.mysiteforme.admin.entity.aiEntity.BusinessRequirement;
import com.mysiteforme.admin.entity.aiEntity.EntityInfo;
import com.mysiteforme.admin.entity.aiEntity.RelationshipInfo;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Async;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AiConfig {

    private final RedisUtils redisUtils;

    private final ChatModel chatModel;

    public AiConfig(RedisUtils redisUtils, ChatModel chatModel) {
        this.redisUtils = redisUtils;
        this.chatModel = chatModel;
    }

    @Bean
    public ChatClient client(){
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个高级人工智能助理，习惯回答问题都1、2、3、...的条列式回答，并且所有回复内容均以简体中文回复")
                .build();
    }

    @Bean
    @Description("根据ChatId获取已存在的业务需求信息")
    public Function<String,BusinessRequirement> getRequirements(){
        return chatId -> {
            try {
                String requirementKey = RedisConstants.AI_REQUIREMENT_KET_PREFIX + chatId;
                if(!redisUtils.hasKey(requirementKey)){
                    return redisUtils.get(requirementKey, BusinessRequirement.class);
                }
                return null;
            }catch (Exception e){
                log.error("获取已存在的业务需求信息出现异常:{}",e.getMessage());
                throw MyException.builder().businessError("获取已存在的业务需求信息出现异常").build();
            }
        };
    }

    @Bean
    @Description("保存业务需求信息")
    public Function<BusinessRequirement, String> saveRequirement(){
        return request -> {
            log.debug("正在调用保存业务需求信息:{}",request);
            this.saveRequirementRedisData(request);
            return "";
        };
    }

    @Bean
    @Description("更新业务需求信息")
    public Function<BusinessRequirement, String> updateRequirement(){
        return request -> {
            try {
                String requirementKey = RedisConstants.AI_REQUIREMENT_KET_PREFIX + request.getChatId();
                redisUtils.set(requirementKey,request);
                redisUtils.expire(requirementKey, 1, TimeUnit.DAYS);
                String entityKey = RedisConstants.AI_REQUIREMENT_ENTITY_KET_PREFIX + request.getChatId();
                redisUtils.del(entityKey);
                String relationShipKey = RedisConstants.AI_REQUIREMENT_RELATIONSHIP_KET_PREFIX + request.getChatId();
                redisUtils.del(relationShipKey);
                String additionalKey = RedisConstants.AI_REQUIREMENT_ADDITIONAL_KET_PREFIX + request.getChatId();
                redisUtils.del(additionalKey);
                return "";
            }catch (Exception e){
                log.error("更新业务需求信息出现异常:{}",e.getMessage());
                throw MyException.builder().businessError("更新业务需求信息出现异常").build();
            }
        };
    }

    @Bean
    @Description("根据ChatId获取实体列表信息")
    public Function<String, List<EntityInfo>> getEntityList(){
        return chatId -> {
            try {
                String entityKey = RedisConstants.AI_REQUIREMENT_ENTITY_KET_PREFIX + chatId;
                if(!redisUtils.hasKey(entityKey)){
                    List<Object> result = redisUtils.lRange(entityKey,0, -1);
                    return result.stream().map(m -> {
                        if(m instanceof EntityInfo entity){
                            return entity;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                }
                return Collections.emptyList();
            }catch (Exception e){
                log.error("根据ChatId获取实力列表信息出现异常:{}",e.getMessage());
                throw MyException.builder().businessError("根据ChatId获取实力列表信息出现异常").build();
            }
        };
    }

    @Bean
    @Description("保存新增实体信息")
    public Function<EntityInfo, String> saveEntity(){
        return request -> {
            log.debug("正在调用保存新增实体信息:{}",request);
            this.saveEntityRedisData(request);
            return "";
        };
    }

    @Bean
    @Description("根据ChatId获取实体之间的关系信息列表")
    public Function<String, List<RelationshipInfo>> getRelationshipList(){
        return chatId -> {
            try {
                String relationShipKey = RedisConstants.AI_REQUIREMENT_RELATIONSHIP_KET_PREFIX + chatId;
                if(!redisUtils.hasKey(relationShipKey)){
                    List<Object> result = redisUtils.lRange(relationShipKey,0, -1);
                    return result.stream().map(m -> {
                                if(m instanceof RelationshipInfo relationship){
                                    return relationship;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
            }catch (Exception e){
                log.error("获取实体之间的关系信息列表出现异常:{}",e.getMessage());
                throw MyException.builder().businessError("获取实体之间的关系信息列表出现异常").build();
            }
        };
    }

    @Bean
    @Description("保存实体之间的关系信息")
    public Function<RelationshipInfo, String> saveRelationship(){
        return request -> {
            log.debug("正在调用保存实体之间的关系信息:{}",request);
            this.saveRelationshipRedisData(request);
            return "";
        };
    }

    @Bean
    @Description("根据ChatId获取额外需求列表")
    public Function<String, List<String>> getAdditionalRequirementList(){
        return chatId -> {
            try {
                String additionalKey = RedisConstants.AI_REQUIREMENT_ADDITIONAL_KET_PREFIX + chatId;
                if(!redisUtils.hasKey(additionalKey)){
                    List<Object> result = redisUtils.lRange(additionalKey,0, -1);
                    return result.stream().map(m -> {
                                if(m instanceof String addition){
                                    return addition;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
            }catch (Exception e){
                log.error("根据ChatId获取额外需求列表出现异常:{}",e.getMessage());
                throw MyException.builder().businessError("根据ChatId获取额外需求列表出现异常").build();
            }
        };
    }

    @Bean
    @Description("保存额外需求信息")
    public Function<Additional, String> saveAdditional(){
        return request -> {
            log.debug("正在调用保存额外需求信息:{}",request);
            this.saveAdditionalRedisData(request);
            return "";
        };
    }


    @Async
    public void saveRequirementRedisData(BusinessRequirement requirement){
        try {
            String requirementKey = RedisConstants.AI_REQUIREMENT_KET_PREFIX + requirement.getChatId();
            redisUtils.set(requirementKey,requirement);
            redisUtils.expire(requirementKey, 1, TimeUnit.DAYS);
        }catch (Exception e){
            log.error("保存业务需求信息出现异常:{}",e.getMessage());
            throw MyException.builder().businessError("保存业务需求信息出现异常").build();
        }
    }

    @Async
    public void saveEntityRedisData(EntityInfo entityInfo){
        try {
            String entityKey = RedisConstants.AI_REQUIREMENT_ENTITY_KET_PREFIX + entityInfo.getChatId();
            redisUtils.leftPushList(entityKey, entityInfo);
            redisUtils.expire(entityKey, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("保存新增实体信息出现异常:{}", e.getMessage());
            throw MyException.builder().businessError("保存新增实体信息出现异常").build();
        }
    }

    @Async
    public void saveRelationshipRedisData(RelationshipInfo relationshipInfo){
        try {
            String relationShipKey = RedisConstants.AI_REQUIREMENT_RELATIONSHIP_KET_PREFIX + relationshipInfo.getChatId();
            redisUtils.leftPushList(relationShipKey, relationshipInfo);
            redisUtils.expire(relationShipKey, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("保存实体之间的关系信息出现异常:{}", e.getMessage());
            throw MyException.builder().businessError("保存实体之间的关系信息出现异常").build();
        }
    }

    @Async
    public void saveAdditionalRedisData(Additional additional){
        try {
            String additionalKey = RedisConstants.AI_REQUIREMENT_ADDITIONAL_KET_PREFIX + additional.getChatId();
            redisUtils.leftPushList(additionalKey, additional.getAdditionalRequirement());
            redisUtils.expire(additionalKey, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("保存额外需求信息出现异常:{}", e.getMessage());
            throw MyException.builder().businessError("保存额外需求信息出现异常").build();
        }
    }


}
