package com.example.chatbot_used_market.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
  @NotNull
  private Long revieweeId;

  @NotNull
  private Long tradeId;

  @NotNull
  private Integer rating;

  private String content;
}
