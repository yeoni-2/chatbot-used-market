package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.entity.Message;
import com.example.chatbot_used_market.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatroomListDto {
    private Long chatroomId;
    private String opponentNickname;
    private String opponentProfileImgUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;

    public ChatroomListDto(Chatroom chatroom, User opponentUser, Message lastMessage, long unreadCount) {
        this.chatroomId = chatroom.getId();
        this.opponentNickname = opponentUser.getNickname();
        this.opponentProfileImgUrl = opponentUser.getProfileImgUrl();

        if (lastMessage != null) {
            this.lastMessage = lastMessage.getContent();
            this.lastMessageTime = lastMessage.getCreatedAt();
            this.unreadCount = unreadCount;
        } else {
            this.lastMessage = "대화 시작하기";
            this.lastMessageTime = chatroom.getUpdatedAt();
        }
    }
}
