package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.ChatbotDto;
import com.example.chatbot_used_market.service.ChatbotService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ChatbotDto.Response> getChatbotReply(@RequestBody ChatbotDto.Request request) {
        // TODO: 실제 로그인한 사용자 ID를 가져와야 합니다.
        Long currentUserId = 1L;
        String botReply = chatbotService.getChatbotReply(currentUserId, request.getUserMessage());
        ChatbotDto.Response response = new ChatbotDto.Response(botReply);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public String chatbotPage() { return "chatbot"; }
}
