package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.entity.Trade;
import lombok.Getter;

@Getter
public class ChatroomDetailDto {
    private Long tradeId;
    private String tradeTitle;
    private Integer tradePrice;
    private String tradeImgUrl;
    private String tradeStatus;
    private Long sellerId;

    public ChatroomDetailDto(Chatroom chatroom) {
        Trade trade = chatroom.getTrade();

        this.tradeId = trade.getId();
        this.tradeTitle = trade.getTitle();
        this.tradePrice = trade.getPrice();
        //this.tradeImgUrl = trade.getTradeImgUrl(); // Trade 엔티티에 getTradeImgUrl()이 있다고 가정
        this.tradeStatus = trade.getStatus();
        this.sellerId = chatroom.getSeller().getId();
    }
}
