package com.example.chatbot_used_market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "username", unique = true, length = 50)
    private String username;

    @Column(name = "nickname", unique = true, length = 50)
    private String nickname;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "profile_img_url", length = 100)
    private String profileImgUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Trade> sellingTrades = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Trade> buyingTrades = new ArrayList<>();

    public User() {
    }

    public User(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }

    public User(String email, String nickname, String password, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.providerId = providerId;
    }
}