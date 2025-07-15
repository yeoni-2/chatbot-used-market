package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatroomDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private Long tradeId;
    }

    @Getter
    public static class Response {
        private Long chatroomId;

        public Response(Long chatroomId) {
            this.chatroomId = chatroomId;
        }
    }
}
