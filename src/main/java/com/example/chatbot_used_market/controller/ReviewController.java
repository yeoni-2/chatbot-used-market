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
  public String reviewForm(@RequestParam("trade_id") Long tradeId, HttpSession session, Model model){
    Long userId = (Long)session.getAttribute("loginUserId");
    Trade trade = tradeService.findById(tradeId);

    if (trade == null){
      return "redirect:/trades";
    }

    if (!trade.getStatus().equals("판매완료")){
      return "redirect:/trades";
    }

    Long buyerId = trade.getBuyer().getId();
    Long sellerId = trade.getSeller().getId();

    if (!buyerId.equals(userId) && !sellerId.equals(userId)){
      return "redirect:/trades";
    }

    model.addAttribute("trade", trade);
    model.addAttribute("revieweeId", buyerId.equals(userId) ? sellerId : buyerId);

    return "reviewForm";
  }

  @PostMapping
  public String createReview(@Valid @ModelAttribute ReviewDto reviewDto,
                             HttpSession session){
    try {
      Long userId = (Long)session.getAttribute("loginUserId");
      User reviewer = userService.findById(userId);
      User reviewee = userService.findById(reviewDto.getRevieweeId());
      Trade trade = tradeService.findById(reviewDto.getTradeId());

      if (userId==null || reviewer==null){
        return "redirect:/trades";
      }

      // 거래완료 상태가 아닌 거래는 리뷰를 작성할 수 없음
      if (!trade.getStatus().equals("거래완료")){
        return "redirect:/trades";
      }

      // 거래와 관계있는 사람만 리뷰를 작성할 수 있음
      if (!isRelatedUser(trade, reviewer)){
        return "redirect:/trades";
      }
      if (!isRelatedUser(trade, reviewee)){
        return "redirect:/trades";
      }

      reviewService.createReview(reviewer, reviewee, trade, reviewDto.getRating(), reviewDto.getContent());

      return "redirect:/trades";
    } catch (Exception e) {
      return "redirect:/trades";
    }
  }

  @DeleteMapping("/{id}")
  public String deleteReview(@PathVariable("id") Long reviewId,
                             HttpSession session){
    User user = userService.findById((Long)session.getAttribute("loginUserId"));

    if (!reviewService.isAuthor(reviewId, user)){
      return "redirect:/trades";
    }

    reviewService.deleteReviewById(reviewId);

    return "redirect:/trade";
  }

  private boolean isRelatedUser(Trade trade, User user){
    if (trade.getSeller().equals(user)) return true;
    return trade.getBuyer().equals(user);
  }
}
