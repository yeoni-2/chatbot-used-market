package com.example.chatbot_used_market.config;

import com.example.chatbot_used_market.handler.ChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat") // "/ws/chat" 경로로 요청이 오면
                .setAllowedOrigins("*");     // 모든 도메인에서의 접속을 허용
    }
}
