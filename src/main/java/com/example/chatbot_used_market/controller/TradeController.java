package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String createTrade(@ModelAttribute TradeRequestDto requestDto) {
        // 임시로 첫 번째 사용자를 판매자로 설정 (나중에 로그인 시스템으로 대체)
        User seller = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.createTrade(requestDto, seller);
        // 작성 완료 후 상세 페이지로 리다이렉트
        return "redirect:/trades/" + responseDto.getId();
    }
    
    // 거래글 상세 페이지
    @GetMapping("/{id}")
    public String tradeDetail(@PathVariable Long id, Model model) {
        // 조회수 증가
        tradeService.incrementViewCount(id);
        
        TradeResponseDto trade = tradeService.getTradeById(id);
        model.addAttribute("trade", trade);
        return "trade_post";
    }
    
    // 거래글 수정 페이지
    @GetMapping("/{id}/edit")
    public String editTradeForm(@PathVariable Long id, Model model) {
        TradeResponseDto trade = tradeService.getTradeById(id);
        model.addAttribute("trade", trade);
        return "write";
    }
    
    // 거래글 수정 처리
    @PostMapping("/{id}")
    public String updateTradePost(@PathVariable Long id, @ModelAttribute TradeRequestDto requestDto) {
        // 임시로 첫 번째 사용자를 판매자로 설정 (나중에 로그인 시스템으로 대체)
        User seller = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.updateTrade(id, requestDto, seller);
        // 수정 완료 후 상세 페이지로 리다이렉트
        return "redirect:/trades/" + responseDto.getId();
    }
    
    // 거래글 삭제
    @GetMapping("/{id}/delete")
    public String deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        // 삭제 완료 후 목록 페이지로 리다이렉트
        return "redirect:/trades";
    }
}