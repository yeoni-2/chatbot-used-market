package com.example.chatbot_used_market.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    public Message(String content, User sender, Chatroom chatroom) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.sender = sender;
        this.chatroom = chatroom;
    }
}
