package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeRequestDto {
    private String title;
    private Integer price;
    private String category;
    private String description;
    // tradeImgUrl 관련 코드 완전 삭제
}