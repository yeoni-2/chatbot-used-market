package com.example.chatbot_used_market.dto;

import com.example.chatbot_used_market.entity.Chatroom;
import lombok.Getter;

@Getter
public class ChatroomListDto {
    private Long chatroomId;
    private String opponentNickname;

    public ChatroomListDto(Chatroom chatroom, Long currentUserId) {
        this.chatroomId = chatroom.getId();
        if (currentUserId.equals(chatroom.getSeller().getId())) {
            this.opponentNickname = chatroom.getBuyer().getNickname();
        } else {
            this.opponentNickname = chatroom.getSeller().getNickname();
        }
    }
}
