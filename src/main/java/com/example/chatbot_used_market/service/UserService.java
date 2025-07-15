package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.User;

public interface UserService {
    User findById(Long id);
    boolean isUsernameDuplicate(String username);
    boolean isNicknameDuplicate(String nickname);
    boolean isEmailDuplicate(String email);
    boolean isValidUsername(String username);
    boolean isValidNickname(String nickname);
    boolean isValidPassword(String password);
    void saveUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
}