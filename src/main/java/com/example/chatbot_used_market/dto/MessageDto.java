package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Message;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageDto {
    private Long messageId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private LocalDateTime createdAt;

    public MessageDto(Message message) {
        this.messageId = message.getId();
        this.senderId = message.getSender().getId();
        this.senderNickname = message.getSender().getNickname();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }
}
