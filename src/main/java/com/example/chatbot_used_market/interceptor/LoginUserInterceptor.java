package com.example.chatbot_used_market.interceptor;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginUserInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public LoginUserInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();

        // 이미 세션에 저장된 경우 스킵
        if (session.getAttribute("loginUserId") != null) {
            return true;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identifier = null;

        if (principal instanceof UserDetails userDetails) {
            identifier = userDetails.getUsername();
        } else if (principal instanceof OAuth2User oauth2User) {
            identifier = oauth2User.getAttribute("email");
        }

        if (identifier != null) {
            User user = userService.findByUsername(identifier);
            if (user == null) {
                user = userService.findByEmail(identifier);
            }

            if (user != null) {
                session.setAttribute("loginUserId", user.getId());
                session.setAttribute("loginUsername", user.getUsername());
                session.setAttribute("loginNickname", user.getNickname());
                session.setAttribute("loginEmail", user.getEmail());
                session.setAttribute("loginProfileImgUrl", user.getProfileImgUrl());
            }
        }

        return true;
    }
}