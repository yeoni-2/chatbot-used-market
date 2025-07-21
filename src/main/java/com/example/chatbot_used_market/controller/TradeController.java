package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.TradeRequestDto;
import com.example.chatbot_used_market.dto.TradeResponseDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        
        return "trade";
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
    public String createTrade(@ModelAttribute TradeRequestDto requestDto, HttpSession session) {
        // 로그인된 사용자 정보 가져오기
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
        
        User seller = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TradeResponseDto responseDto = tradeService.createTrade(requestDto, seller);
        
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
    public String updateTradePost(@PathVariable Long id, @ModelAttribute TradeRequestDto requestDto, HttpSession session) {
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
        
        TradeResponseDto responseDto = tradeService.updateTrade(id, requestDto, seller);
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

    // 무한스크롤 API
    @GetMapping("/api")
    @ResponseBody
    public Page<TradeResponseDto> getPagedTrades(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "8") int size) {
        return tradeService.getPagedTrades(page, size);
    }
}