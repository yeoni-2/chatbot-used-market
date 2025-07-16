package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, WebClient webClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.webClient = webClient;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isUsernameDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isValidUsername(String username) {
        return Pattern.matches("^[a-zA-Z0-9]{6,20}$", username);
    }

    @Override
    public boolean isValidNickname(String nickname) {
        return Pattern.matches("^[가-힣a-zA-Z0-9]{4,12}$", nickname);
    }

    @Override
    public boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()]{8,20}$", password);
    }

    @Override
    public void saveUser(User user) {
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
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

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
