package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TradeResponseDto {
    private Long id;
    private Long sellerId;
    private String sellerUsername;
    private String sellerNickname;
    private String title;
    private Integer price;
    private String category;
    private String description;
    private List<String> imageUrls; // 모든 이미지 URL 리스트
    private String status;
    private Integer viewCount;
    private Integer chatCount;
    private LocalDateTime createdAt;
}
