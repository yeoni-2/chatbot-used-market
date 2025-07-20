package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.ReviewDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.ReviewService;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
  private final ReviewService reviewService;
  private final UserServiceImpl userService;
  private final TradeService tradeService;

  public ReviewController(ReviewService reviewService, UserServiceImpl userService, TradeService tradeService){
    this.reviewService = reviewService;
    this.userService = userService;
    this.tradeService = tradeService;
  }

  @GetMapping
  public String reviewForm(@RequestParam("trade_id") Long tradeId, Model model){
    Trade trade = tradeService.findById(tradeId);

    if (trade == null){
      return "error";
    }

    model.addAttribute("trade", trade);

    return "reviewForm";
  }

  @PostMapping
  public String createReview(@Valid @RequestBody ReviewDto reviewDto,
                             HttpSession session,
                             @AuthenticationPrincipal OAuth2User oAuth2User){
    try {
//      User reviewer = AuthService.getCurrentUser(session, oAuth2User);
      User reviewer = userService.findById(3L);
      User reviewee = userService.findById(reviewDto.getRevieweeId());
      Trade trade = tradeService.findById(reviewDto.getTradeId());

      // 거래완료 상태가 아닌 거래는 리뷰를 작성할 수 없음
//      if (!trade.getStatus().equals("거래완료")){
//        return "error";
//      }

      // 거래와 관계있는 사람만 리뷰를 작성할 수 있음
//      if (!isRelatedUser(trade, reviewer)){
//        return "error";
//      }
//      if (!isRelatedUser(trade, reviewee)){
//        return "error";
//      }

      reviewService.createReview(reviewer, reviewee, trade, reviewDto.getRating(), reviewDto.getContent());

      return "trade";
    } catch (Exception e) {
      return "error";
    }
  }

  @DeleteMapping("/{id}")
  public String deleteReview(@PathVariable("id") Long reviewId,
                             HttpSession session,
                             @AuthenticationPrincipal OAuth2User oauth2User){
//    User user = AuthService.getCurrentUser(session, oauth2User);
//
//    if (!reviewService.isAuthor(reviewId, user)){
//      return "error";
//    }

    reviewService.deleteReviewById(reviewId);

    return null;
  }

  private boolean isRelatedUser(Trade trade, User user){
    if (trade.getSeller().equals(user)) return true;
    return trade.getBuyer().equals(user);
  }
}
