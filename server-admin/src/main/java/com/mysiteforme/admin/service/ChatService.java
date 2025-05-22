package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.DTO.ChatHistoryDTO;
import com.mysiteforme.admin.entity.request.ChatRequest;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;


public interface ChatService {

    /**
     * 流式获取AI返回的消息
     */
    Flux<String> getMessage(ChatRequest request, String deviceId);

    /**
     * 获取聊天历史记录
     */
    List<ChatHistoryDTO> getHistoryList(String deviceId, int limit);
}
