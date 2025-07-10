package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.User;

import java.util.List;

public interface TradeService {
    List<TradeResponseDto> getAllTrades();
    TradeResponseDto createTrade(TradeRequestDto requestDto, User seller);
    TradeResponseDto getTradeById(Long id);
    TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, User seller);
    void deleteTrade(Long id);
    void incrementViewCount(Long id);
}
