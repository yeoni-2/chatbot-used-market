package com.example.chatbot_used_market.config;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoginUserAdvice {

    private final UserService userService;

    public LoginUserAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addLoginUserToSession(@AuthenticationPrincipal Object principal, HttpSession session, Model model) {
        if (session.getAttribute("loginUserId") != null) {
            return;
        }

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
                session.setAttribute("loginUser", user);
            }
        }
    }
}