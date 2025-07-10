package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

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
    public String tradeList(Model model) {
        List<TradeResponseDto> trades = tradeService.getAllTrades();
        model.addAttribute("trades", trades);
        return "trade";
    }
    
    @GetMapping("/write")
    public String writeForm() {
        return "write";
    }
    
    // 거래글 작성 처리
    @PostMapping
    public String createTrade(@ModelAttribute TradeRequestDto requestDto, HttpSession session) {
        // 임시로 첫 번째 사용자를 판매자로 설정 (나중에 로그인 시스템으로 대체)
        User seller = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.createTrade(requestDto, seller);
        
        // 세션에 작성자 정보 저장 (포스트 ID와 작성자 ID를 매핑)
        String authorKey = "author_" + responseDto.getId();
        session.setAttribute(authorKey, seller.getId());
        
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
        
        // 현재 세션의 사용자가 작성자인지 확인
        String authorKey = "author_" + id;
        Long sessionAuthorId = (Long) session.getAttribute(authorKey);
        boolean isAuthor = sessionAuthorId != null && sessionAuthorId.equals(trade.getSellerId());
        model.addAttribute("isAuthor", isAuthor);
        
        return "trade_post";
    }
    
    // 거래글 수정 페이지
    @GetMapping("/{id}/edit")
    public String editTradeForm(@PathVariable Long id, Model model, HttpSession session) {
        // 작성자 확인
        String authorKey = "author_" + id;
        Long sessionAuthorId = (Long) session.getAttribute(authorKey);
        TradeResponseDto trade = tradeService.getTradeById(id);
        
        if (sessionAuthorId == null || !sessionAuthorId.equals(trade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        model.addAttribute("trade", trade);
        return "write";
    }
    
    // 거래글 수정 처리
    @PostMapping("/{id}")
    public String updateTradePost(@PathVariable Long id, @ModelAttribute TradeRequestDto requestDto, HttpSession session) {
        // 작성자 확인
        String authorKey = "author_" + id;
        Long sessionAuthorId = (Long) session.getAttribute(authorKey);
        TradeResponseDto existingTrade = tradeService.getTradeById(id);
        
        if (sessionAuthorId == null || !sessionAuthorId.equals(existingTrade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        // 임시로 첫 번째 사용자를 판매자로 설정 (나중에 로그인 시스템으로 대체)
        User seller = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.updateTrade(id, requestDto, seller);
        // 수정 완료 후 상세 페이지로 리다이렉트
        return "redirect:/trades/" + responseDto.getId();
    }
    
    // 거래글 삭제
    @GetMapping("/{id}/delete")
    public String deleteTrade(@PathVariable Long id, HttpSession session) {
        // 작성자 확인
        String authorKey = "author_" + id;
        Long sessionAuthorId = (Long) session.getAttribute(authorKey);
        TradeResponseDto existingTrade = tradeService.getTradeById(id);
        
        if (sessionAuthorId == null || !sessionAuthorId.equals(existingTrade.getSellerId())) {
            return "redirect:/trades/" + id; // 작성자가 아니면 상세 페이지로 리다이렉트
        }
        
        tradeService.deleteTrade(id);
        // 세션에서 작성자 정보 제거
        session.removeAttribute(authorKey);
        // 삭제 완료 후 목록 페이지로 리다이렉트
        return "redirect:/trades";
    }
}