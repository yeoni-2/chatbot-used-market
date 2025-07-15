package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketMessage {
    public enum MessageType { JOIN, TALK }
    private MessageType type;
    private Long chatroomId;
    private Long senderId;  // 실제 구현에서는 토큰에서 추출
    private String content;
}
