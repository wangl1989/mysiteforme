package com.mysiteforme.admin.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    @Bean
    @Primary
    public HttpClient aiHttpClient() {
        log.info("正在调用noPoolHttpClient=============");
        // 方法一：使用不启用池化的 ConnectionProvider (更推荐)
        // ConnectionProvider.newConnection() 每次都会创建一个新连接，而不是从池中获取
        ConnectionProvider provider = ConnectionProvider.builder("ai-connection-pool")
                .maxConnections(50)
                .maxIdleTime(Duration.ofMinutes(5)) // 增加空闲时间到5分钟
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .evictInBackground(Duration.ofSeconds(30)) // 定期清理过期连接
                .build();

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true) // 启用TCP keepalive
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));
    }

}
