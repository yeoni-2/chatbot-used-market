package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {

    private final UserService userService;

    public OAuth2Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User oauthUser, HttpSession session) {
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String providerId = oauthUser.getAttribute("sub");

        User user = userService.findByEmail(email);
        if (user == null) {
            String nickname = userService.isNicknameDuplicate(name) ? null : name;

            user = new User(email, nickname, null, providerId);
            userService.saveUser(user);
        }

        session.setAttribute("user", user);

        return "redirect:/trade";
    }
}