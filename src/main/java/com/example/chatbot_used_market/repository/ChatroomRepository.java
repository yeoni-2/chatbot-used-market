package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    // productId, buyerId, sellerId로 채팅방을 찾는 메서드
    Optional<Chatroom> findByTradeIdAndBuyerIdAndSellerId(Long tradeId, Long buyerId, Long sellerId);
}
