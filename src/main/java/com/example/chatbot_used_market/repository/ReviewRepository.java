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

  @Query(value = "SELECT * FROM reviews WHERE reviewer=:userId OR reviewee=:userId", nativeQuery = true)
  List<Review> findByUserId(@Param("userId") Long userId);

//  @Query(value = "SELECT r.*, u.id AS uid, u.username, u.nickname " +
//          "FROM (SELECT * FROM reviews WHERE reviewee=:userId) AS r " +
//          "LEFT JOIN users u ON r.reviewee=u.id", nativeQuery = true)
  @Query(value = "SELECT r FROM Review r " +
          "JOIN FETCH r.trade t " +
          "WHERE r.reviewee.id=:userId ")
  List<Review> findReceivedReviewByUserId(@Param("userId") Long userId);

//  @Query(value = "SELECT r.*, u.username, u.nickname, t.title, t.price " +
//          "FROM (SELECT * FROM reviews WHERE reviewer=:userId) AS r " +
//          "LEFT JOIN users u ON r.reviewer=u.id " +
//          "LEFT JOIN trades t ON r.trade_id=t.id", nativeQuery = true)
  @Query(value = "SELECT r FROM Review r " +
          "JOIN FETCH r.trade t " +
          "WHERE r.reviewer.id=:userId")
  List<Review> findWrittenReviewByUserId(@Param("userId") Long userId);
}
