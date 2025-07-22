package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeStatusUpdateDto {
    private String status;
    private Long buyerId;
}
