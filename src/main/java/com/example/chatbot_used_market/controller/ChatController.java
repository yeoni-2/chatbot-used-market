package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.ChatroomDetailDto;
import com.example.chatbot_used_market.dto.ChatroomDto;
import com.example.chatbot_used_market.dto.ChatroomListDto;
import com.example.chatbot_used_market.dto.MessageDto;
import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성 또는 조회
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createOrGetChatroom(@RequestBody ChatroomDto.Request request, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("loginUserId");
        if (currentUserId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        try {
            Chatroom chatroom = chatService.createOrGetChatroom(request.getTradeId(), currentUserId);
            ChatroomDto.Response response = new ChatroomDto.Response(chatroom.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/messages")
    @ResponseBody
    public ResponseEntity<Slice<MessageDto>> getMessages(@PathVariable("id") Long chatroomId, Pageable pageable) {
        Slice<MessageDto> messages = chatService.getMessages(chatroomId, pageable);

        return ResponseEntity.ok(messages);
    }

    // 메인 채팅 페이지
    @GetMapping("/page")    // URL: GET /chats/page
    public String chatPage(Model model, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        if (loginUserId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("loginUserId", loginUserId);

        return "chats";
    }

    // 내 채팅방 목록 조회 API
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ChatroomListDto>> getMyChatrooms(HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("loginUserId");
        List<ChatroomListDto> chatrooms = chatService.findMyChatrooms(currentUserId);

        return ResponseEntity.ok(chatrooms);
    }

    @GetMapping("/{id}/details")
    @ResponseBody
    public ResponseEntity<ChatroomDetailDto> getChatroomDetails(@PathVariable("id") Long chatroomId) {
        ChatroomDetailDto details = chatService.getChatroomDetails(chatroomId);

        return ResponseEntity.ok(details);
    }
}
