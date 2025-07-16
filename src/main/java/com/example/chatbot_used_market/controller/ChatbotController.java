package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.ChatbotDto;
import com.example.chatbot_used_market.dto.ChatbotMessageDto;
import com.example.chatbot_used_market.service.ChatbotService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping("/messages")
    @ResponseBody
    public ResponseEntity<ChatbotDto.Response> getChatbotReply(@RequestBody ChatbotDto.Request request, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("loginUserId");
        String botReply = chatbotService.getChatbotReply(currentUserId, request.getUserMessage());
        ChatbotDto.Response response = new ChatbotDto.Response(botReply);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<Slice<ChatbotMessageDto>> getChatbotMessages(HttpSession session, Pageable pageable) {
        Long currentUserId = (Long) session.getAttribute("loginUserId");
        Slice<ChatbotMessageDto> messages = chatbotService.getChatbotMessages(currentUserId, pageable);

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/page")
    public String chatbotPage() { return "chatbot"; }
}
