package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.TradeImage;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.TradeImageRepository;
import com.example.chatbot_used_market.repository.TradeRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.S3Service;
import com.example.chatbot_used_market.service.TradeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {
    
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final TradeImageRepository tradeImageRepository;
    private final S3Service s3Service;

    public TradeServiceImpl(TradeRepository tradeRepository, UserRepository userRepository,
                           TradeImageRepository tradeImageRepository, S3Service s3Service) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.tradeImageRepository = tradeImageRepository;
        this.s3Service = s3Service;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TradeResponseDto> getAllTrades() {
        List<Trade> trades = tradeRepository.findByStatusOrderByViewCountDesc("판매중");
        return trades.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TradeResponseDto> searchTradesByKeyword(String keyword) {
        List<Trade> trades = tradeRepository.findByTitleContainingAndStatusOrderByViewCountDesc(keyword, "판매중");
        return trades.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeResponseDto> searchTradesByKeywordAndCategory(String keyword, String category) {
        List<Trade> trades = tradeRepository.findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(keyword, category, "판매중");
        return trades.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeResponseDto> searchTradesByKeywordWithPagination(String keyword, Pageable pageable) {
        Page<Trade> trades = tradeRepository.findByTitleContainingAndStatusOrderByViewCountDesc(keyword, "판매중", pageable);
        return trades.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeResponseDto> searchTradesByKeywordAndCategoryWithPagination(String keyword, String category, Pageable pageable) {
        Page<Trade> trades = tradeRepository.findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(keyword, category, "판매중", pageable);
        return trades.map(this::convertToResponseDto);
    }

    @Override
    @Transactional
    public TradeResponseDto createTrade(TradeRequestDto requestDto, List<MultipartFile> images, Long sellerId) {
        // 사용자 조회 및 검증
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        return createTrade(requestDto, images, seller);
    }

    @Override
    @Transactional
    public TradeResponseDto createTrade(TradeRequestDto requestDto, List<MultipartFile> images, User seller) {
        // 1. 이미지 파일 사전 검증
        if (images != null && !images.isEmpty() && !(images.size()==1 && images.get(0).getOriginalFilename().isEmpty())) {
            validateImages(images);
        }

        // 2. 이미지 업로드 (거래글 저장 전에 먼저 업로드)
        List<String> uploadedImageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty() && !(images.size()==1 && images.get(0).getOriginalFilename().isEmpty())) {
            try {
                uploadedImageUrls = uploadImagesWithRollback(images);
            } catch (Exception e) {
                // 이미지 업로드 실패 시 이미 업로드된 이미지들 삭제
                cleanupUploadedImages(uploadedImageUrls);
                throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage(), e);
            }
        }

        try {
            // 3. 거래글 생성 및 저장
            Trade trade = new Trade();
            trade.setTitle(requestDto.getTitle());
            trade.setPrice(requestDto.getPrice());
            trade.setCategory(requestDto.getCategory());
            trade.setDescription(requestDto.getDescription());
            trade.setSeller(seller);

            Trade savedTrade = tradeRepository.save(trade);

            // 4. 이미지 URL을 DB에 저장
            if (!uploadedImageUrls.isEmpty()) {
                try {
                    saveTradeImages(savedTrade, uploadedImageUrls);
                } catch (Exception e) {
                    // DB 저장 실패 시 업로드된 이미지들 삭제
                    cleanupUploadedImages(uploadedImageUrls);
                    throw new RuntimeException("이미지 정보 저장에 실패했습니다: " + e.getMessage(), e);
                }
            }

            return convertToResponseDto(savedTrade);

        } catch (Exception e) {
            // 거래글 저장 실패 시 업로드된 이미지들 삭제
            cleanupUploadedImages(uploadedImageUrls);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, List<MultipartFile> images, Long currentUserId) {
        // 거래글 존재 확인
        if (!existsById(id)) {
            throw new RuntimeException("거래글을 찾을 수 없습니다.");
        }
        
        // 작성자 권한 확인
        validateTradeAuthor(id, currentUserId);
        
        // 사용자 조회
        User seller = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        return updateTrade(id, requestDto, images, seller);
    }

    @Override
    @Transactional
    public void deleteTrade(Long id, Long currentUserId) {
        // 거래글 존재 확인
        if (!existsById(id)) {
            throw new RuntimeException("거래글을 찾을 수 없습니다.");
        }
        
        // 작성자 권한 확인
        validateTradeAuthor(id, currentUserId);
        
        deleteTrade(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTradeAuthor(Long tradeId, Long userId) {
        if (userId == null) {
            return false;
        }
        
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            return false;
        }
        
        return userId.equals(trade.getSeller().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateTradeAuthor(Long tradeId, Long userId) {
        if (userId == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        
        if (!isTradeAuthor(tradeId, userId)) {
            throw new SecurityException("작성자만 수정/삭제할 수 있습니다.");
        }
    }

    @Override
    @Transactional
    public TradeResponseDto updateTradeStatus(Long tradeId, String status, Long currentUserId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + tradeId));

        if (!trade.getSeller().getId().equals(currentUserId))
            throw new SecurityException("거래 상태를 변경할 권한이 없습니다.");

        trade.setStatus(status);
        Trade updatedTrade = tradeRepository.save(trade);

        return convertToResponseDto(updatedTrade);
    }

    @Override
    @Transactional
    public TradeResponseDto updateTrade(Long id, TradeRequestDto requestDto, List<MultipartFile> images, User seller) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        // 기존 이미지 정보 백업
        List<TradeImage> existingImages = tradeImageRepository.findByTradeId(id);
        List<String> existingImageUrls = existingImages.stream()
                .map(TradeImage::getUrl)
                .toList();

        List<String> uploadedImageUrls = new ArrayList<>();

        try {
            // 새로운 이미지가 있는 경우에만 처리
            if (images != null && !images.isEmpty()) {
                // 1. 새 이미지 파일 검증
                validateImages(images);

                // 2. 새 이미지 업로드
                uploadedImageUrls = uploadImagesWithRollback(images);

                // 3. 기존 이미지 삭제 (S3에서)
                cleanupUploadedImages(existingImageUrls);

                // 4. 기존 이미지 정보 DB에서 삭제
                tradeImageRepository.deleteByTradeId(id);

                // 5. 새 이미지 정보 DB에 저장
                saveTradeImages(trade, uploadedImageUrls);
            }

            // 6. 거래글 기본 정보 업데이트
            trade.setTitle(requestDto.getTitle());
            trade.setPrice(requestDto.getPrice());
            trade.setCategory(requestDto.getCategory());
            trade.setDescription(requestDto.getDescription());

            Trade updatedTrade = tradeRepository.save(trade);

            return convertToResponseDto(updatedTrade);

        } catch (Exception e) {
            // 실패 시 롤백: 새로 업로드된 이미지 삭제
            if (!uploadedImageUrls.isEmpty()) {
                cleanupUploadedImages(uploadedImageUrls);
            }

            throw new RuntimeException("거래글 수정에 실패했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteTrade(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        // 연관된 이미지들 삭제
        List<TradeImage> images = tradeImageRepository.findByTradeId(id);
        List<String> imageUrls = images.stream()
                .map(TradeImage::getUrl)
                .toList();

        // S3에서 이미지 삭제
        cleanupUploadedImages(imageUrls);

        // DB에서 이미지 정보 삭제
        tradeImageRepository.deleteByTradeId(id);

        // 거래글 삭제
        tradeRepository.delete(trade);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        
        trade.setViewCount(trade.getViewCount() + 1);
        tradeRepository.save(trade);
    }

    @Override
    public Page<TradeResponseDto> getPagedTrades(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        Page<Trade> tradesPage = tradeRepository.findByStatus("판매중", pageable);

        return tradesPage.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TradeResponseDto getTradeById(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        return convertToResponseDto(trade);
    }


    /**
     * 이미지 파일들을 사전 검증
     */
    private void validateImages(List<MultipartFile> images) {
        if (images.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5장까지 업로드 가능합니다.");
        }

        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);

            if (file.isEmpty()) {
                throw new IllegalArgumentException("빈 파일이 포함되어 있습니다. (파일 " + (i + 1) + ")");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. (파일 " + (i + 1) + ": " + file.getOriginalFilename() + ")");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다. (파일 " + (i + 1) + ": " + file.getOriginalFilename() + ")");
            }
        }
    }

    /**
     * 이미지 업로드 (실패 시 자동 롤백)
     */
    private List<String> uploadImagesWithRollback(List<MultipartFile> images) {
        List<String> uploadedUrls = new ArrayList<>();

        try {
            for (MultipartFile file : images) {
                String imageUrl = s3Service.uploadImage(file);
                uploadedUrls.add(imageUrl);
            }

            return uploadedUrls;

        } catch (Exception e) {
            // 실패 시 이미 업로드된 이미지들 삭제
            cleanupUploadedImages(uploadedUrls);

            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 업로드된 이미지들을 S3에서 삭제
     */
    private void cleanupUploadedImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        for (String imageUrl : imageUrls) {
            try {
                s3Service.deleteImage(imageUrl);
            } catch (Exception e) {
                // 개별 이미지 삭제 실패는 전체 작업을 중단시키지 않음
            }
        }
    }

    /**
     * 거래 이미지 정보를 DB에 저장
     */
    private void saveTradeImages(Trade trade, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            TradeImage tradeImage = new TradeImage();
            tradeImage.setTrade(trade);
            tradeImage.setUrl(imageUrl);
            tradeImageRepository.save(tradeImage);
        }
    }

    @Transactional(readOnly = true)
    private TradeResponseDto convertToResponseDto(Trade trade) {
        TradeResponseDto responseDto = new TradeResponseDto();
        responseDto.setId(trade.getId());
        responseDto.setSellerId(trade.getSeller().getId());
        responseDto.setSellerUsername(trade.getSeller().getUsername());
        responseDto.setSellerNickname(trade.getSeller().getNickname());
        responseDto.setSellerLocation(trade.getSeller().getLocation());
        responseDto.setTitle(trade.getTitle());
        responseDto.setPrice(trade.getPrice());
        responseDto.setCategory(trade.getCategory());
        responseDto.setDescription(trade.getDescription());

        // 모든 이미지 URL 설정
        List<TradeImage> tradeImages = tradeImageRepository.findByTradeId(trade.getId());
        if (!tradeImages.isEmpty()) {
            List<String> imageUrls = tradeImages.stream()
                    .map(TradeImage::getUrl)
                    .toList();
            responseDto.setImageUrls(imageUrls);
        } else {
            responseDto.setImageUrls(List.of());
        }

        responseDto.setStatus(trade.getStatus());
        responseDto.setViewCount(trade.getViewCount());

        // 채팅 수 계산 (실제 구현에서는 ChatroomRepository에서 조회)
        responseDto.setChatCount(0); // 임시로 0으로 설정

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
