package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface TradeService {
    List<TradeResponseDto> getAllTrades();
    List<TradeResponseDto> searchTradesByKeyword(String keyword);
    List<TradeResponseDto> searchTradesByKeywordAndCategory(String keyword, String category);

    // 페이지네이션을 지원하는 검색 메소드들
    Page<TradeResponseDto> searchTradesByKeywordWithPagination(String keyword, Pageable pageable);
    Page<TradeResponseDto> searchTradesByKeywordAndCategoryWithPagination(String keyword, String category, Pageable pageable);

    TradeResponseDto createTrade(TradeRequestDto requestDto, List<MultipartFile> images, User seller);
    TradeResponseDto getTradeById(Long id);
    TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, List<MultipartFile> images, User seller);
    void deleteTrade(Long id);
    void incrementViewCount(Long id);

    Page<TradeResponseDto> getPagedTrades(int page, int size);
    Trade findById(Long id);
    boolean existsById(Long id);
    TradeResponseDto updateTradeStatus(Long tradeId, String status, Long buyerId, Long currentUserId);
    List<Trade> findTradesByUserIdAndStatus(Long tradeId, String status);
}
