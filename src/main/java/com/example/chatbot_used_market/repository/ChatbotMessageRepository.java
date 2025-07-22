package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.ChatbotMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {
    Slice<ChatbotMessage> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
