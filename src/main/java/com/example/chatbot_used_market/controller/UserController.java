package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.UserSignupDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("user") UserSignupDto dto, Model model) {
        // 비밀번호 대조
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }
        // 아이디 중복 검사
        if (userService.isUsernameDuplicate(dto.getUsername())) {
            model.addAttribute("error", "아이디가 중복인지 확인해 주세요.");
            return "signup";
        }
        // 닉네임 중복 검사
        if (dto.getNickname() != null && !dto.getNickname().isBlank() &&
                userService.isNicknameDuplicate(dto.getNickname())) {
            model.addAttribute("error", "닉네임이 중복인지 확인해 주세요.");
            return "signup";
        }

        // 유효성 검사
        if (!userService.isValidUsername(dto.getUsername())) {
            model.addAttribute("error", "아이디는 영문/숫자를 사용한 6~20자만 가능합니다.");
            return "signup";
        }

        if (!dto.getNickname().isBlank() && !userService.isValidNickname(dto.getNickname())) {
            model.addAttribute("error", "닉네임은 한글/영문/숫자를 사용한 4~12자만 가능합니다.");
            return "signup";
        }

        if (!userService.isValidPassword(dto.getPassword())) {
            model.addAttribute("error", "비밀번호는 영문+숫자 필수, 특수문자는 !@#$%^&*()만 가능하며 8~20자여야 합니다.");
            return "signup";
        }

        User user = new User(dto.getUsername(), dto.getNickname(), dto.getPassword());
        userService.saveUser(user);

        return "redirect:/login?signupSuccess=true";
    }

    // 중복 확인 API
    @ResponseBody
    @GetMapping("/check/username")
    public boolean checkUsername(@RequestParam String username) {
        return userService.isUsernameDuplicate(username);
    }

    @ResponseBody
    @GetMapping("/check/nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameDuplicate(nickname);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/trade")
    public String tradePage(Model model, @AuthenticationPrincipal Object principal) {
        String username = null;
        User user = null;

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            username = userDetails.getUsername();
            user = userService.findByUsername(username);
        } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            username = oauth2User.getAttribute("email");
            user = userService.findByEmail(username);
        }

        if (user != null) {
            model.addAttribute("hasNickname", user.getNickname() != null && !user.getNickname().isBlank());
        } else {
            model.addAttribute("hasNickname", false);
        }

        return "trade";
    }

    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }
}
