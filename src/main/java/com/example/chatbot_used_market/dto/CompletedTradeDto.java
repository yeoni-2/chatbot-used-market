package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CompletedTradeDto {
  private Long id;
  private User seller;
  private User buyer;
  private String title;
  private String description;
  private Integer price;
  private String category;
  private String status;
  private Integer viewCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean written;

  public CompletedTradeDto(Trade trade, boolean written){
    this.id = trade.getId();
    this.seller = trade.getSeller();
    this.buyer = trade.getBuyer();
    this.title = trade.getTitle();
    this.description = trade.getDescription();
    this.price = trade.getPrice();
    this.category = trade.getCategory();
    this.status = trade.getCategory();
    this.viewCount = trade.getViewCount();
    this.createdAt = trade.getCreatedAt();
    this.updatedAt = trade.getUpdatedAt();
    this.written = written;
  }
}