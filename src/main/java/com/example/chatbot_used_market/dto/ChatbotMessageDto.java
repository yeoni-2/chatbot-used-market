package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.ChatbotMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatbotMessageDto {
    private String userMessage;
    private String botResponse;
    private LocalDateTime createdAt;

    public ChatbotMessageDto(ChatbotMessage message) {
        this.userMessage = message.getUserMessage();
        this.botResponse = message.getBotResponse();
        this.createdAt = message.getCreatedAt();
    }
}
