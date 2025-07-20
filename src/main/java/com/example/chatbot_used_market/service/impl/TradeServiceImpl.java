package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.TradeRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.TradeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {
    
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    
    public TradeServiceImpl(TradeRepository tradeRepository, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public List<TradeResponseDto> getAllTrades() {
        List<Trade> trades = tradeRepository.findByStatusOrderByViewCountDesc("판매중");
        return trades.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    @Override
    public TradeResponseDto createTrade(TradeRequestDto requestDto, User seller) {
        Trade trade = new Trade();
        trade.setTitle(requestDto.getTitle());
        trade.setPrice(requestDto.getPrice());
        trade.setCategory(requestDto.getCategory());
        trade.setDescription(requestDto.getDescription());
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
    
    @Override
    public TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, User seller) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        // 기존 데이터 업데이트
        trade.setTitle(requestDto.getTitle());
        trade.setPrice(requestDto.getPrice());
        trade.setCategory(requestDto.getCategory());
        trade.setDescription(requestDto.getDescription());
        
        Trade updatedTrade = tradeRepository.save(trade);
        return convertToResponseDto(updatedTrade);
    }
    
    @Override
    public void deleteTrade(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        tradeRepository.delete(trade);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        trade.setViewCount(trade.getViewCount() + 1);
        tradeRepository.save(trade);
    }
    
    private TradeResponseDto convertToResponseDto(Trade trade) {
        TradeResponseDto responseDto = new TradeResponseDto();
        responseDto.setId(trade.getId());
        responseDto.setSellerId(trade.getSeller().getId()); // 작성자 ID 설정
        responseDto.setTitle(trade.getTitle());
        responseDto.setPrice(trade.getPrice());
        responseDto.setCategory(trade.getCategory());
        responseDto.setDescription(trade.getDescription());
        responseDto.setStatus(trade.getStatus());
        responseDto.setViewCount(trade.getViewCount());
        responseDto.setCreatedAt(trade.getCreatedAt());
        
        return responseDto;
    }

    @Override
    public Trade findById(Long id) {
        return tradeRepository.findById(id).orElse(null);
    }

    public boolean existsById(Long id){
        return tradeRepository.existsById(id);
    }
}
