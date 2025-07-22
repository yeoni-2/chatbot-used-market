package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.dto.TradeStatusUpdateDto;
import com.example.chatbot_used_market.service.TradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    //거래글 목록 페이지
    @GetMapping("/trades")
    public String tradeList(Model model, HttpSession session) {
        // 로그인된 사용자 ID 조회
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        // 위치 기반 필터링 적용 (로그인했고 위치 인증된 사용자)
        Page<TradeResponseDto> page;
        if (loginUserId != null) {
            // 사용자가 위치 인증했으면 5km 반경 내 게시글만 조회
            page = tradeService.getNearbyTradesWithPaginationByUserId(loginUserId, PageRequest.of(0, 8));
        } else {
            // 비로그인 사용자는 전체 게시글 조회
            page = tradeService.getPagedTrades(0, 8);
        }

        model.addAttribute("trades", page.getContent());
        model.addAttribute("hasNext", !page.isLast());
        model.addAttribute("loginUserId", loginUserId);

        // hasNickname 변수 추가 (기본값 false)
        model.addAttribute("hasNickname", false);

        return "trade";
    }

    //메인 페이지 거래글 목록
    @GetMapping("/main")
    public String mainPage(Model model) {
        Page<TradeResponseDto> page = tradeService.getPagedTrades(0, 8);
        model.addAttribute("trades", page.getContent());
        return "main";
    }

    // 검색 기능
    @GetMapping("/trades/search")
    public String searchTrades(@RequestParam String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "8") int size,
                              Model model, HttpSession session) {
        // 페이지네이션을 위한 Pageable 객체 생성 (page는 0부터 시작)
        Pageable pageable = PageRequest.of(page - 1, size);

        // 로그인된 사용자 ID 조회
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        Page<TradeResponseDto> tradesPage;

        // 위치 기반 필터링 적용
        if (loginUserId != null) {
            // 로그인한 사용자: 위치 기반 검색 (5km 반경)
            if (category != null && !category.isEmpty()) {
                // 키워드 + 카테고리 + 위치 기반 검색
                tradesPage = tradeService.searchNearbyTradesByKeywordAndCategoryAndUserId(keyword, category, loginUserId, pageable);
            } else {
                // 키워드 + 위치 기반 검색
                tradesPage = tradeService.searchNearbyTradesByKeywordAndUserId(keyword, loginUserId, pageable);
            }
        } else {
            // 비로그인 사용자: 전체 검색
            if (category != null && !category.isEmpty()) {
                // 키워드와 카테고리 모두로 검색
                tradesPage = tradeService.searchTradesByKeywordAndCategoryWithPagination(keyword, category, pageable);
            } else {
                // 키워드만으로 검색
                tradesPage = tradeService.searchTradesByKeywordWithPagination(keyword, pageable);
            }
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
        model.addAttribute("loginUserId", loginUserId);

        return "search";
    }

    @GetMapping("/trades/write")
    public String writeForm(HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        return "write";
    }

    // 거래글 작성 처리
    @PostMapping("/trades")
    public String createTrade(@ModelAttribute TradeRequestDto requestDto,
                             @RequestParam(value = "images", required = false) List<MultipartFile> images,
                             HttpSession session) {
        // 로그인된 사용자 ID 가져오기
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        try {
            TradeResponseDto responseDto = tradeService.createTrade(requestDto, images, loginUserId);
            // 작성 완료 후 상세 페이지로 리다이렉트
            return "redirect:/trades/" + responseDto.getId();
        } catch (Exception e) {
            // 에러 처리 (실제로는 에러 페이지로 리다이렉트하거나 메시지 표시)
            return "redirect:/trades/write?error=" + e.getMessage();
        }

    }

    // 거래글 상세 페이지
    @GetMapping("/trades/{id}")
    public String tradeDetail(@PathVariable Long id, Model model, HttpSession session) {

        try {
            // 조회수 증가
            tradeService.incrementViewCount(id);

            TradeResponseDto trade = tradeService.getTradeById(id);
            model.addAttribute("trade", trade);

            // 로그인된 사용자가 작성자인지 확인
            Long loginUserId = (Long) session.getAttribute("loginUserId");
            boolean isAuthor = tradeService.isTradeAuthor(id, loginUserId);
            model.addAttribute("isAuthor", isAuthor);

            return "trade_post";
        } catch (Exception e) {
            return "redirect:/trades?error=" + e.getMessage();
        }
    }

    // 거래글 수정 페이지
    @GetMapping("/trades/{id}/edit")
    public String editTradeForm(@PathVariable Long id, Model model, HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        
        try {
            // 작성자 권한 확인 (서비스에서 처리)
            tradeService.validateTradeAuthor(id, loginUserId);

            TradeResponseDto trade = tradeService.getTradeById(id);
            model.addAttribute("trade", trade);
            return "write";
        } catch (SecurityException e) {
            return "redirect:/trades/" + id + "?error=unauthorized";
        } catch (Exception e) {
            return "redirect:/trades?error=" + e.getMessage();
        }
    }

    // 거래글 수정 처리
    @PostMapping("/trades/{id}")
    public String updateTradePost(@PathVariable Long id, @ModelAttribute TradeRequestDto requestDto,
                                 @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                 HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        try {
            TradeResponseDto responseDto = tradeService.updateTrade(id, requestDto, images, loginUserId);
            // 수정 완료 후 상세 페이지로 리다이렉트
            return "redirect:/trades/" + responseDto.getId();
        } catch (SecurityException e) {
            return "redirect:/trades/" + id + "?error=unauthorized";
        } catch (Exception e) {
            return "redirect:/trades/" + id + "/edit?error=" + e.getMessage();
        }

    }

    // 거래글 삭제
    @GetMapping("/trades/{id}/delete")
    public String deleteTrade(@PathVariable Long id, HttpSession session) {
        // 로그인 확인
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        try {
            tradeService.deleteTrade(id, loginUserId);
            // 삭제 완료 후 목록 페이지로 리다이렉트
            return "redirect:/trades";
        } catch (SecurityException e) {
            return "redirect:/trades/" + id + "?error=unauthorized";
        } catch (Exception e) {
            return "redirect:/trades?error=" + e.getMessage();
        }
    }

    // --- 거래 상태 변경 API 추가 ---
    @PatchMapping("/trades/{id}/status")
    @ResponseBody
    public ResponseEntity<TradeResponseDto> updateTradeStatus(
            @PathVariable Long id,
            @RequestBody TradeStatusUpdateDto requestDto,
            HttpSession session) {

        Long loginUserId = (Long) session.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            TradeResponseDto updatedTrade = tradeService.updateTradeStatus(id, requestDto.getStatus(), requestDto.getBuyerId(), loginUserId);
            return ResponseEntity.ok(updatedTrade);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // 무한스크롤 API
    @GetMapping("/trades/api")
    @ResponseBody
    public Page<TradeResponseDto> getPagedTrades(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "8") int size,
                                                 HttpSession session) {
        // 로그인된 사용자 ID 조회
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        // 위치 기반 필터링 적용
        if (loginUserId != null) {
            // 로그인한 사용자: 위치 기반 조회 (5km 반경)
            return tradeService.getNearbyTradesWithPaginationByUserId(loginUserId, PageRequest.of(page, size));
        } else {
            // 비로그인 사용자: 전체 조회
            return tradeService.getPagedTrades(page, size);
        }
    }
}