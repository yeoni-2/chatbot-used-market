package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatusOrderByViewCountDesc(String status);
}
