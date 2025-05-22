package com.mysiteforme.admin.service.impl;

import com.mysiteforme.admin.config.ChatRedisMemory;
import com.mysiteforme.admin.entity.DTO.ChatDTO;
import com.mysiteforme.admin.entity.DTO.ChatHistoryDTO;
import com.mysiteforme.admin.entity.request.ChatRequest;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final RedisUtils redisUtils;

    private final ChatClient client;

    private final Integer CHAT_HISTORY_SIZE = 100;

    public ChatServiceImpl(RedisUtils redisUtils, ChatClient client) {
        this.redisUtils = redisUtils;
        this.client = client;
    }

    @Override
    public List<ChatHistoryDTO> getHistoryList(String deviceId, int limit) {
        String deviceKey = RedisConstants.AI_DEVICE_HISTORY_KEY_PREFIX + deviceId;
        if(!redisUtils.hasKey(deviceKey)){
            return Collections.emptyList();
        }
        Set<Object> deviceChatIds = redisUtils.zRange(deviceKey,0,limit-1);
        return deviceChatIds.stream()
                .map(o -> {
                    if(o instanceof String chatId) {
                        ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();
                        chatHistoryDTO.setChatId(chatId);
                        String historyKey = RedisConstants.AI_HISTORY_KEY_PREFIX + chatId;
                        List<Object> historyList = redisUtils.lRange(historyKey, 0, -1);
                        List<ChatDTO> chatList =  historyList.stream().map(h -> {
                            if(h instanceof ChatDTO chat){
                                return chat;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
                        chatHistoryDTO.setChatList(chatList);
                        return chatHistoryDTO;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Flux<String> getMessage(ChatRequest request, String deviceId) {
        ChatRedisMemory chatRedisMemory = new ChatRedisMemory(redisUtils,deviceId);
        return client.prompt(request.getContent())
                .advisors(new MessageChatMemoryAdvisor(chatRedisMemory))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, request.getChatId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_HISTORY_SIZE)
                )
                .stream()
                .content()
                .onErrorResume(e -> {
                    log.error("流式响应异常 (after retries or non-retryable) for chatId {}: {}", request.getChatId(), e.getMessage(), e);
                    return Flux.just("[系统提示] AI 服务通讯异常，请稍后重试或联系管理员。");
                });
    }
}
