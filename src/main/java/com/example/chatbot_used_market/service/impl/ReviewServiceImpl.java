package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.entity.Review;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.ReviewRepository;
import com.example.chatbot_used_market.service.ReviewService;
import com.example.chatbot_used_market.util.GeometryUtil;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;

  public ReviewServiceImpl(ReviewRepository reviewRepository){
    this.reviewRepository = reviewRepository;
  }

  @Override
  public void createReview(User reviewer, User reviewee, Trade trade, Integer rating, String content) {
    Review review = new Review(reviewer, reviewee, trade, rating, content);

    reviewRepository.save(review);
//    return reviewRepository.save(review);
  }

  @Override
  public Review findReviewById(Long id) {
    return reviewRepository.findById(id).orElse(null);
  }

  @Override
  public List<Review> findReceivedReviewsByUserId(Long userId) {
    return reviewRepository.findReceivedReviewByUserId(userId);
  }

  @Override
  public List<Review> findWrittenReviewsByUserId(Long userId) {
    return reviewRepository.findWrittenReviewByUserId(userId);
  }

  @Override
  public List<Review> findReviewsByTradeId(Long tradeId) {
    return reviewRepository.findByTradeId(tradeId);
  }

  @Override
  public List<Review> findReviewsByUserId(Long userId) {
    return reviewRepository.findByUserId(userId);
  }

  @Override
  public void deleteReviewById(Long id) {
    reviewRepository.deleteById(id);
  }

  @Override
  public boolean isAuthor(Long reviewId, User user) {
    Review review = reviewRepository.findById(reviewId).orElse(null);

    if (review == null) throw new Error("Review "+reviewId+" not found");

    if (!review.getReviewer().equals(user)){
      return false;
    }

    return true;
  }
}
