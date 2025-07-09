package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.User;

public interface TradeService {
    TradeResponseDto createTrade(TradeRequestDto requestDto, User seller);
    TradeResponseDto getTradeById(Long id);
}
