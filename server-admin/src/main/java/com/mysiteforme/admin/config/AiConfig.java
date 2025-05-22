package com.mysiteforme.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AiConfig {

    private final ChatModel originalChatModel;

    public AiConfig(ChatModel originalChatModel) {
        this.originalChatModel = originalChatModel;
    }

    @Bean
    public ChatClient client(){
        return ChatClient.builder(originalChatModel)
                .defaultSystem("你是一个高级人工智能助理，习惯回答问题都1、2、3、...的条列式回答，并且所有回复内容均以简体中文回复")
                .build();
    }

}
