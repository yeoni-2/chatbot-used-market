package com.example.chatbot_used_market.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatbot_messages")
public class ChatbotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_message", length = 255)
    private String userMessage;

    @Column(name = "bot_response", length = 255)
    private String botResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ChatbotMessage(User user, String userMessage, String botResponse) {
        this.user = user;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.createdAt = LocalDateTime.now();
    }
}
