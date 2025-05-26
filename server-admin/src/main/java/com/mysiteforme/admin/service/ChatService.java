package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.DTO.ChatHistoryDTO;
import com.mysiteforme.admin.entity.aiEntity.BusinessRequirement;
import com.mysiteforme.admin.entity.request.ChatRequest;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;


public interface ChatService {

    /**
     * 流式获取AI返回的消息
     */
    Flux<String> getMessage(ChatRequest request);

    /**
     * 获取聊天历史记录
     */
    List<ChatHistoryDTO> getHistoryList(String deviceId, int limit);

    /**
     * 从用户对话中提取业务需求信息
     */
    Flux<String> analyzeUserRequirements(ChatRequest request);

    /**
     * 收集用户确认和补充信息
     */
    Flux<String> generateConfirmationQuestions(ChatRequest request);

    /**
     * 根据用户反馈优化需求
     */
    BusinessRequirement refineRequirements(BusinessRequirement originalRequirement, String userFeedback);
}
