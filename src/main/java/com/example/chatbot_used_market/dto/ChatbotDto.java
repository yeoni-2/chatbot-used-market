package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

public class ChatbotDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request { private String userMessage; }

    @Getter
    public static class Response {
        private String reply;

        public Response(String reply) { this.reply = reply; }
    }
}
