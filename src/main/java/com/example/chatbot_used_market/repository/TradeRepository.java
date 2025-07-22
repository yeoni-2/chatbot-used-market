package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatusOrderByViewCountDesc(String status);

    // 제목에 키워드가 포함된 게시글 검색
    List<Trade> findByTitleContainingAndStatusOrderByViewCountDesc(String keyword, String status);

    // 제목에 키워드가 포함되고 특정 카테고리인 게시글 검색
    List<Trade> findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(String keyword, String category, String status);

    // 페이지네이션을 지원하는 검색 메소드들
    Page<Trade> findByTitleContainingAndStatusOrderByViewCountDesc(String keyword, String status, Pageable pageable);
    Page<Trade> findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(String keyword, String category, String status, Pageable pageable);

    Page<Trade> findByStatus(String status, Pageable pageable);

    @Query(value = "SELECT * FROM trades WHERE (seller_id=:userId OR buyer_id=:userId) AND status=:status", nativeQuery = true)
    List<Trade> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}
