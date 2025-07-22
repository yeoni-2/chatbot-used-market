package com.example.chatbot_used_market.controller;

import com.example.chatbot_used_market.dto.CompletedTradeDto;
import com.example.chatbot_used_market.dto.UserLocationDto;
import com.example.chatbot_used_market.dto.UserSignupDto;
import com.example.chatbot_used_market.entity.Review;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.service.ReviewService;
import com.example.chatbot_used_market.service.TradeService;
import com.example.chatbot_used_market.service.UserService;
import com.example.chatbot_used_market.util.GeometryUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final TradeService tradeService;

    public UserController(UserService userService, ReviewService reviewService, TradeService tradeService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.tradeService = tradeService;
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

//    @GetMapping("/main")
//    public String mainPage() {
//        return "main";
//    }

    @GetMapping("/users/{id}")
    public String userProfile(@PathVariable("id") Long targetUserId, HttpSession session, Model model){
        Long userId = (Long)session.getAttribute("loginUserId");

        if (targetUserId == null || !targetUserId.equals(userId)){
            return "redirect:/users/"+userId;
        }

        User user = userService.findById(userId);

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        List<Review> receivedReviews = reviewService.findReceivedReviewsByUserId(userId);
        List<Review> writtenReviews = reviewService.findWrittenReviewsByUserId(userId);
        List<Trade> sellingTrades = tradeService.findTradesByUserIdAndStatus(userId, "판매중");
        List<Trade> completedTrades = tradeService.findTradesByUserIdAndStatus(userId, "거래완료");
        List<CompletedTradeDto> completedTradeDtoList = completedTrades.stream()
                .map(trade -> {
                    boolean isReviewWritten = reviewService.existsByTradeIdAndReviewerId(trade.getId(), userId);
                    return new CompletedTradeDto(trade, isReviewWritten);
                }).collect(Collectors.toList());

        model.addAttribute("receivedReviews", receivedReviews);
        model.addAttribute("writtenReviews", writtenReviews);
        model.addAttribute("sellingTrades", sellingTrades);
        model.addAttribute("completedTrades", completedTradeDtoList);

        return "userProfile";
    }

    @GetMapping("/users/{id}/locations")
    public String userLocation(@PathVariable("id") Long targetUserId, HttpSession session, Model model){
        Long userId = (Long)session.getAttribute("loginUserId");

        if (targetUserId == null || !targetUserId.equals(userId)){
            return "redirect:/users/"+userId+"/locations";
        }

        model.addAttribute("loginUserId", userId);

        return "location";
    }

    @PostMapping("/users/{id}/locations")
    public Mono<String> authUserLocation(@Valid UserLocationDto userLocationDto,
                                         @PathVariable(name = "id") Long targetUserId,
                                         HttpSession session,
                                         Model model) {
        Long userId = (Long)session.getAttribute("loginUserId");

        if (userId == null){
            return Mono.just("redirect:/login");
        }

        if (targetUserId == null || !targetUserId.equals(userId)) {
            return Mono.just("redirect:/users/"+userId+"/locations");
        }

        return userService
                .googleGeocodingByLocation(userLocationDto.getLocation())
                .map(result -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode locationNode = objectMapper.readTree(result)
                                .get("results").get(0)
                                .get("geometry").get("location");
                        double latitude = locationNode.get("lat").asDouble();
                        double longitude = locationNode.get("lng").asDouble();
                        System.out.println(result);
                        System.out.println(latitude);
                        System.out.println(longitude);
                        System.out.println(userLocationDto.getLocation());
                        // distance <= 2km
                        if (GeometryUtil.isInDistance(
                                userLocationDto.getLatitude(),
                                userLocationDto.getLongitude(),
                                latitude, longitude, 2)) {
                            userService.updatePositionAndLocationById(userId, GeometryUtil.getPoint(latitude, longitude), userLocationDto.getDongName());

                            return "redirect:/main";
                        } else {
                            model.addAttribute("error", "현재 위치와 입력하신 주소 간의 거리가 너무 멉니다.");
                            model.addAttribute("loginUserId", userId);

                            return "location";
                        }
                    } catch (Exception e) {
                        return "redirect:/users/"+userId;
                    }
                });
    }

    @GetMapping("/test")
    public String asdf(){
        System.out.println(GeometryUtil.isInDistance(127.171291, 37.367713, 127.159615, 37.368464, 2));

        return "redirect:/main";
    }
}
