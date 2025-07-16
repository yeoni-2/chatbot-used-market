package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.dto.ChatroomListDto;
import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.dto.MessageDto;
import com.example.chatbot_used_market.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatService {
    Chatroom createOrGetChatroom(Long tradeId, Long buyerId);
    Slice<MessageDto> getMessages(Long chatroomId, Pageable pageable);
    Message createMessage(Long chatroomId, Long senderId, String content);
    List<ChatroomListDto> findMyChatrooms(Long userId);
}
