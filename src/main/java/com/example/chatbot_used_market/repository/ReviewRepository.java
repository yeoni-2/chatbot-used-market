package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByTradeId(Long tradeId);

  @Query(value = "SELECT * FROM USER WHERE reviewer=:userId OR reviewee=:userId", nativeQuery = true)
  List<Review> findByUserId(@Param("userId") Long userId);
}
