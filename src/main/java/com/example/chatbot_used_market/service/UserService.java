package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.User;
import org.locationtech.jts.geom.Point;
import reactor.core.publisher.Mono;

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
  Mono<String> googleGeocodingByLocation(String location);
  void updatePositionAndLocationById(Long id, Point point, String location);
  boolean isLocationVerified(Long userId);
}
