package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.TradeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeImageRepository extends JpaRepository<TradeImage, Long> {
    
    List<TradeImage> findByTradeId(Long tradeId);
    void deleteByTradeId(Long tradeId);
} 