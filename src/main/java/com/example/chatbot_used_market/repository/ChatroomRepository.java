package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    // productId, buyerId, sellerId로 채팅방을 찾는 메서드
    Optional<Chatroom> findByTradeIdAndBuyerIdAndSellerId(Long tradeId, Long buyerId, Long sellerId);
    // 특정 사용자가 포함된 모든 채팅방을 찾는 메서드
    List<Chatroom> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}
