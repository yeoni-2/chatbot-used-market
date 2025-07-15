package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.UserService;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WebClient webClient;

    public UserServiceImpl(UserRepository userRepository, WebClient webClient) {
        this.userRepository = userRepository;
        this.webClient = webClient;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 중복 아이디 검사
    @Override
    public boolean isUsernameDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    // 중복 닉네임 검사
    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 아이디 유효성 검사 (영문, 숫자 6~20자)
    @Override
    public boolean isValidUsername(String username) {
        return Pattern.matches("^[a-zA-Z0-9]{6,20}$", username);
    }

    // 닉네임 유효성 검사 (한글, 영문, 숫자 4~12자)
    @Override
    public boolean isValidNickname(String nickname) {
        return Pattern.matches("^[가-힣a-zA-Z0-9]{4,12}$", nickname);
    }

    // 비밀번호 유효성 검사 (영문+숫자 필수, 특수문자 !@#$%^&*()만 허용, 8~20자)
    @Override
    public boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()]{8,20}$", password);
    }

    // 회원 정보 저장
    @Override
    public void saveUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Mono<String> googleGeocodingByLocation(String location){
        return webClient
                .get()
                .uri("?key={apiKey}&address="+String.join(",", location.split(" ")))
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public void updatePositionById(Long id, double latitude, double longitude) {
        userRepository.updatePositionById(id, String.format("POINT(%f %f)", longitude, latitude));
    }
}
