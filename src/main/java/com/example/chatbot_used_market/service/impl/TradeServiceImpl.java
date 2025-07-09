package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.TradeRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.TradeService;
import org.springframework.stereotype.Service;

@Service
public class TradeServiceImpl implements TradeService {
    
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    
    public TradeServiceImpl(TradeRepository tradeRepository, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public TradeResponseDto createTrade(TradeRequestDto requestDto, User seller) {
        Trade trade = new Trade();
        trade.setTitle(requestDto.getTitle());
        trade.setPrice(requestDto.getPrice());
        trade.setCategory(requestDto.getCategory());
        trade.setDescription(requestDto.getDescription());
        trade.setTradeImgUrl(requestDto.getTradeImgUrl());
        trade.setSeller(seller);
        
        Trade savedTrade = tradeRepository.save(trade);
        return convertToResponseDto(savedTrade);
    }
    
    @Override
    public TradeResponseDto getTradeById(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        return convertToResponseDto(trade);
    }
    
    private TradeResponseDto convertToResponseDto(Trade trade) {
        TradeResponseDto responseDto = new TradeResponseDto();
        responseDto.setId(trade.getId());
        responseDto.setTitle(trade.getTitle());
        responseDto.setPrice(trade.getPrice());
        responseDto.setCategory(trade.getCategory());
        responseDto.setDescription(trade.getDescription());
        responseDto.setTradeImgUrl(trade.getTradeImgUrl());
        responseDto.setStatus(trade.getStatus());
        responseDto.setViewCount(trade.getViewCount());
        responseDto.setCreatedAt(trade.getCreatedAt());
        
        return responseDto;
    }
}
