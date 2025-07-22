package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.service.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final ChatService chatService;

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("loginUserId");
        if (currentUserId == null) return ResponseEntity.ok(0L); // Unauthorized response

        long count = chatService.getUnreadMessageCount(currentUserId);
        return ResponseEntity.ok(count);
    }
}
