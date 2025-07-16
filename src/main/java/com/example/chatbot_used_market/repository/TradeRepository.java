package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatusOrderByViewCountDesc(String status);
    
    // 제목에 키워드가 포함된 게시글 검색
    List<Trade> findByTitleContainingAndStatusOrderByViewCountDesc(String keyword, String status);
    
    // 제목에 키워드가 포함되고 특정 카테고리인 게시글 검색
    List<Trade> findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(String keyword, String category, String status);
}