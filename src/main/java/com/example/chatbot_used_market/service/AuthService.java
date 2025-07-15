package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
  public static User getCurrentUser(HttpSession session, OAuth2User oAuth2User){
    // 1. OAuth2 로그인 사용자 확인
    if (oAuth2User != null) {
      Map<String, Object> attributes = oAuth2User.getAttributes();
      Object userObj = attributes.get("user");
      if (userObj instanceof User) {
        return (User) userObj;
      }
    }

    // 2. 기존 세션 로그인 사용자 확인 (하위 호환성)
    Object sessionUser = session.getAttribute("user");
    if (sessionUser instanceof User) {
      return (User) sessionUser;
    }

    return null;
  }
}
