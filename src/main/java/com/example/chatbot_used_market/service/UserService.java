package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 중복 아이디 검사
    public boolean isUsernameDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    // 중복 닉네임 검사
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원 정보 저장
    public void saveUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
