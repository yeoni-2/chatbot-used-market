package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.UserSignupDto;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

        User user = new User(dto.getUsername(), dto.getNickname(), dto.getPassword());
        userService.saveUser(user);

        model.addAttribute("success", true);
        return "signup";
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

    // 로그인
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpServletRequest request
    ) {
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "존재하지 않는 아이디입니다.");
            return "login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "비밀번호가 틀렸습니다.");
            return "login";
        }

        // 로그인 성공 시 세션에 사용자 id 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginUserId", user.getId());

        return "redirect:/trade";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/trade")
    public String tradePage() {
        return "trade";
    }

    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/main";
    }
}
