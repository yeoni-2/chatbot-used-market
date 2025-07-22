package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import org.locationtech.jts.geom.Point;
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

    // 위치 기반 조회 메서드들 (5km 반경)
    List<TradeResponseDto> getNearbyTrades(Point userPosition);
    Page<TradeResponseDto> getNearbyTradesWithPagination(Point userPosition, Pageable pageable);
    Page<TradeResponseDto> searchNearbyTradesByKeyword(String keyword, Point userPosition, Pageable pageable);
    Page<TradeResponseDto> searchNearbyTradesByKeywordAndCategory(String keyword, String category, Point userPosition, Pageable pageable);

    // 사용자 ID를 받아서 위치 기반 조회하는 편의 메서드들
    List<TradeResponseDto> getNearbyTradesByUserId(Long userId);
    Page<TradeResponseDto> getNearbyTradesWithPaginationByUserId(Long userId, Pageable pageable);
    Page<TradeResponseDto> searchNearbyTradesByKeywordAndUserId(String keyword, Long userId, Pageable pageable);
    Page<TradeResponseDto> searchNearbyTradesByKeywordAndCategoryAndUserId(String keyword, String category, Long userId, Pageable pageable);

    // 기존 메서드 (하위 호환성)
    TradeResponseDto createTrade(TradeRequestDto requestDto, List<MultipartFile> images, User seller);
    TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, List<MultipartFile> images, User seller);
    void deleteTrade(Long id);
    
    // 개선된 메서드들 - userId만 받아서 서비스에서 처리
    TradeResponseDto createTrade(TradeRequestDto requestDto, List<MultipartFile> images, Long sellerId);
    TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, List<MultipartFile> images, Long currentUserId);
    void deleteTrade(Long id, Long currentUserId);
    
    // 권한 확인 메서드들
    boolean isTradeAuthor(Long tradeId, Long userId);
    void validateTradeAuthor(Long tradeId, Long userId);
    
    TradeResponseDto getTradeById(Long id);
    void incrementViewCount(Long id);

    Page<TradeResponseDto> getPagedTrades(int page, int size);
    Trade findById(Long id);
    boolean existsById(Long id);
    TradeResponseDto updateTradeStatus(Long tradeId, String status, Long buyerId, Long currentUserId);
    List<Trade> findTradesByUserIdAndStatus(Long tradeId, String status);
}
