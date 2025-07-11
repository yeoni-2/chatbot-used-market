package com.example.chatbot_used_market.service.impl;


import com.example.chatbot_used_market.entity.ChatbotMessage;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.ChatbotMessageRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    private final UserRepository userRepository;
    private final ChatbotMessageRepository chatbotMessageRepository;

    @Override
    @Transactional
    public String getChatbotReply(Long userId, String userMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

        // TODO: 여기에 실제 외부 AI (ChatGPT, Gemini) API 호출 로직이 들어갑니다.
        String botResponse = "AI 응답 테스트: '" + userMessage + "'라고 말씀하셨네요.";

        ChatbotMessage chatbotMessage = new ChatbotMessage(user, userMessage, botResponse);

        chatbotMessageRepository.save(chatbotMessage);

        return botResponse;
    }
}
