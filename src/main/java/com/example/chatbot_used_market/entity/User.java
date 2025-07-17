package com.example.chatbot_used_market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private AuthProvider authProvider = AuthProvider.LOCAL;

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

    private String location;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point position;

    public User() {
    }

    public User(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.authProvider = AuthProvider.LOCAL;
    }

    public User(String email, String nickname, String password, String providerId, AuthProvider authProvider) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.providerId = providerId;
        this.authProvider = authProvider;
    }
}