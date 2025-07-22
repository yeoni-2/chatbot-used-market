package com.example.chatbot_used_market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupDto {
    private String username;
    private String nickname;
    private String password;
    private String passwordConfirm;
}
