package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.Review;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;

import java.util.List;

public interface ReviewService {
  void createReview(User reviewer, User reviewee, Trade trade, Integer rating, String content);
  Review findReviewById(Long id);
  List<Review> findReviewsByTradeId(Long tradeId);
  List<Review> findReviewsByUserId(Long userId);
  List<Review> findReceivedReviewsByUserId(Long userId);
  List<Review> findWrittenReviewsByUserId(Long userId);
  void deleteReviewById(Long id);
  boolean isAuthor(Long reviewId, User user);
}
