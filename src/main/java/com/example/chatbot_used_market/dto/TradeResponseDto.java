package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TradeResponseDto {
    private Long id;
    private String title;
    private Integer price;
    private String category;
    private String description;
    private String tradeImgUrl;
    private String status;
    private Integer viewCount;
    private LocalDateTime createdAt;
}
