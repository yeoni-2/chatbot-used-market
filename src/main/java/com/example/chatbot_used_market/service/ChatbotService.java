package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.ChatbotMessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatbotService {
    String getChatbotReply(Long userId, String userMessage);
    Slice<ChatbotMessageDto> getChatbotMessages(Long userId, Pageable pageable);
    void saveConversation(Long userId, String userMessage, String botResponse);
}
