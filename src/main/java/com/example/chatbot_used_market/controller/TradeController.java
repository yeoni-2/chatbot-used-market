package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.dto.TradeStatusUpdateDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/trades")
public class TradeController {
    
    private final TradeService tradeService;
    private final UserRepository userRepository;
    
    public TradeController(TradeService tradeService, UserRepository userRepository) {
        this.tradeService = tradeService;
        this.userRepository = userRepository;
    }
    
    //거래글 목록 페이지
    @GetMapping
    public String tradeList(Model model, HttpSession session) {
        Page<TradeResponseDto> page = tradeService.getPagedTrades(0, 8);

        model.addAttribute("trades", page.getContent());
        model.addAttribute("hasNext", !page.isLast());
        
        // 로그인된 사용자 ID 전달
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        model.addAttribute("loginUserId", loginUserId);
        
        // hasNickname 변수 추가 (기본값 false)
        model.addAttribute("hasNickname", false);

        return "trade";
    }
    
    // 검색 기능
    @GetMapping("/search")
    public String searchTrades(@RequestParam String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "8") int size,
                              Model model, HttpSession session) {
        // 페이지네이션을 위한 Pageable 객체 생성 (page는 0부터 시작)
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TradeResponseDto> tradesPage;

        if (category != null && !category.isEmpty()) {
            // 키워드와 카테고리 모두로 검색
            tradesPage = tradeService.searchTradesByKeywordAndCategoryWithPagination(keyword, category, pageable);
        } else {
            // 키워드만으로 검색
            tradesPage = tradeService.searchTradesByKeywordWithPagination(keyword, pageable);
        }

        // 페이지네이션 정보 계산
        int totalPages = tradesPage.getTotalPages();
        long totalElements = tradesPage.getTotalElements();
        int currentPage = page;

        // 페이지 번호 표시 범위 계산 (현재 페이지 기준 앞뒤로 2페이지씩)
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        model.addAttribute("trades", tradesPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 로그인된 사용자 ID 전달
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        model.addAttribute("loginUserId", loginUserId);

        return "search";
    }

    @GetMapping("/write")
    public String writeForm(HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        return "write";
    }
    
    // 거래글 작성 처리
    @PostMapping
    public String createTrade(@ModelAttribute TradeRequestDto requestDto,
                             @RequestParam(value = "images", required = false) List<MultipartFile> images,
                             HttpSession session) {
        // 로그인된 사용자 정보 가져오기
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        
        User seller = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.createTrade(requestDto, images, seller);
        
        // 작성 완료 후 상세 페이지로 리다이렉트
        return "redirect:/trades/" + responseDto.getId();
    }
    
    // 거래글 상세 페이지
    @GetMapping("/{id}")
    public String tradeDetail(@PathVariable Long id, Model model, HttpSession session) {
        // 조회수 증가
        tradeService.incrementViewCount(id);
        
        TradeResponseDto trade = tradeService.getTradeById(id);
        model.addAttribute("trade", trade);
        
        // 로그인된 사용자가 작성자인지 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        boolean isAuthor = loginUserId != null && loginUserId.equals(trade.getSellerId());
        model.addAttribute("isAuthor", isAuthor);
        
        return "trade_post";
    }
    
    // 거래글 수정 페이지
    @GetMapping("/{id}/edit")
    public String editTradeForm(@PathVariable Long id, Model model, HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        
        // 작성자 확인
        TradeResponseDto trade = tradeService.getTradeById(id);
        if (!loginUserId.equals(trade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        model.addAttribute("trade", trade);
        return "write";
    }
    
    // 거래글 수정 처리
    @PostMapping("/{id}")
    public String updateTradePost(@PathVariable Long id, @ModelAttribute TradeRequestDto requestDto,
                                 @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                 HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        
        // 작성자 확인
        TradeResponseDto existingTrade = tradeService.getTradeById(id);
        if (!loginUserId.equals(existingTrade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        // 로그인된 사용자를 판매자로 설정
        User seller = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.updateTrade(id, requestDto, images, seller);
        // 수정 완료 후 상세 페이지로 리다이렉트
        return "redirect:/trades/" + responseDto.getId();
    }
    
    // 거래글 삭제
    @GetMapping("/{id}/delete")
    public String deleteTrade(@PathVariable Long id, HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        
        // 작성자 확인
        TradeResponseDto existingTrade = tradeService.getTradeById(id);
        if (!loginUserId.equals(existingTrade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        tradeService.deleteTrade(id);
        // 삭제 완료 후 목록 페이지로 리다이렉트
        return "redirect:/trades";
    }

    // --- 거래 상태 변경 API 추가 ---
    @PatchMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<TradeResponseDto> updateTradeStatus(
            @PathVariable Long id,
            @RequestBody TradeStatusUpdateDto requestDto,
            HttpSession session) {

        Long loginUserId = (Long) session.getAttribute("loginUserId");

        if (loginUserId == null)
            return ResponseEntity.status(401).build();

        TradeResponseDto updatedTrade = tradeService.updateTradeStatus(id, requestDto.getStatus(), requestDto.getBuyerId(), loginUserId);

        return ResponseEntity.ok(updatedTrade);
    }

    // 무한스크롤 API
    @GetMapping("/api")
    @ResponseBody
    public Page<TradeResponseDto> getPagedTrades(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "8") int size) {
        return tradeService.getPagedTrades(page, size);
    }
}