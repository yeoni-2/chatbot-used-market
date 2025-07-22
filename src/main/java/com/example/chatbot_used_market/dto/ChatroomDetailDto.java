package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.TradeImage;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatroomDetailDto {
    private Long tradeId;
    private String tradeTitle;
    private Integer tradePrice;
    private String tradeThumbnailUrl;
    private String tradeStatus;
    private Long sellerId;
    private Long buyerId;

    public ChatroomDetailDto(Chatroom chatroom) {
        Trade trade = chatroom.getTrade();

        this.tradeId = trade.getId();
        this.tradeTitle = trade.getTitle();
        this.tradePrice = trade.getPrice();
        this.tradeStatus = trade.getStatus();
        this.sellerId = chatroom.getSeller().getId();
        this.buyerId = chatroom.getBuyer().getId();

        List<TradeImage> tradeImages = chatroom.getTrade().getTradeImages();
        if (tradeImages != null && !tradeImages.isEmpty()) {
            this.tradeThumbnailUrl = tradeImages.get(0).getUrl();
        }
    }
}
