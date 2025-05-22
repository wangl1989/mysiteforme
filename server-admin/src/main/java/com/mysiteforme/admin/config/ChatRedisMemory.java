package com.mysiteforme.admin.config;

import com.mysiteforme.admin.entity.DTO.ChatDTO;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ChatRedisMemory implements ChatMemory {

    private final RedisUtils redisUtils;

    private final String deviceId;

    public ChatRedisMemory(RedisUtils redisUtils,String deviceId) {
        this.redisUtils = redisUtils;
        this.deviceId = deviceId;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = RedisConstants.AI_HISTORY_KEY_PREFIX + conversationId;
        messages.forEach(message -> {
            String[] strs = message.getText().split("</think>");
            String text = strs.length == 2 ? strs[1] : strs[0];

            ChatDTO chat = new ChatDTO();
            chat.setChatId(conversationId);
            chat.setType(message.getMessageType().getValue());
            chat.setText(text);
            chat.setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            redisUtils.rightPushList(key,chat);
            redisUtils.expire(key,1, TimeUnit.DAYS);
        });
        String deviceKey = RedisConstants.AI_DEVICE_HISTORY_KEY_PREFIX + deviceId;
        double timestamp = System.currentTimeMillis();
        redisUtils.zAdd(deviceKey, conversationId, timestamp);
        redisUtils.expire(deviceKey, 1, TimeUnit.DAYS);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = RedisConstants.AI_HISTORY_KEY_PREFIX + conversationId;
        Long size = redisUtils.getListSize(key);
        if(size == null || size == 0){
            return Collections.emptyList();
        }
        int start = Math.max(0, (int) (size - lastN));
        List<Object> listTmp = redisUtils.lRange(key, start , -1);
        // 增加类型检查日志
        if(!CollectionUtils.isEmpty(listTmp)) {
            // 增加转换后校验
            return listTmp.stream().map(obj -> {
                if (obj instanceof ChatDTO chat) {
                    ChatDTO chatDTO = new ChatDTO();
                    BeanUtils.copyProperties(chat, chatDTO);
                    // 增加转换后校验
                    if (chatDTO.getText() == null || chatDTO.getType() == null) {
                        log.warn("发现空值字段 - 类型: {}, 文本: {}", chatDTO.getType(), chatDTO.getText());
                        return null;
                    }
                    if(MessageType.USER.getValue().equals(chatDTO.getType())){
                        return new UserMessage(chatDTO.getText());
                    } else if (MessageType.ASSISTANT.getValue().equals(chatDTO.getType())) {
                        return new AssistantMessage(chatDTO.getText());
                    } else if (MessageType.SYSTEM.getValue().equals(chatDTO.getType())) {
                        return new SystemMessage(chatDTO.getText());
                    } else {
                        ToolResponseMessage.ToolResponse response = new ToolResponseMessage.ToolResponse(conversationId, "", chatDTO.getText());
                        List<ToolResponseMessage.ToolResponse> responses = new ArrayList<>();
                        responses.add(response);
                        return new ToolResponseMessage(responses);
                    }
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void clear(String conversationId) {
        redisUtils.del(RedisConstants.AI_HISTORY_KEY_PREFIX + conversationId);
    }

}
