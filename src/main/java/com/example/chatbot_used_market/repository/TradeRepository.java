package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    // 기본 CRUD만 사용 (save, findById, findAll, delete 등)
}
