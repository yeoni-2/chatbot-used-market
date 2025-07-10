package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 중복 아이디 검사
    public boolean isUsernameDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    // 중복 닉네임 검사
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 아이디 유효성 검사 (영문, 숫자 6~20자)
    public boolean isValidUsername(String username) {
        return Pattern.matches("^[a-zA-Z0-9]{6,20}$", username);
    }

    // 닉네임 유효성 검사 (한글, 영문, 숫자 4~12자)
    public boolean isValidNickname(String nickname) {
        return Pattern.matches("^[가-힣a-zA-Z0-9]{4,12}$", nickname);
    }

    // 비밀번호 유효성 검사 (영문+숫자 필수, 특수문자 !@#$%^&*()만 허용, 8~20자)
    public boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()]{8,20}$", password);
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
