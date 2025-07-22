package com.example.chatbot_used_market.service.impl;


import com.example.chatbot_used_market.dto.ChatbotMessageDto;
import com.example.chatbot_used_market.entity.ChatbotMessage;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.ChatbotMessageRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.ChatbotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {
    private final UserRepository userRepository;
    private final ChatbotMessageRepository chatbotMessageRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.key}")
    private String apiKey;

    @Override
    @Transactional
    public String getChatbotReply(Long userId, String userMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        String modelName = "gemini-1.5-flash-latest";
        String apiUrl = String.format("/v1beta/models/%s:generateContent", modelName);
        String requestBody = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", userMessage);
        Mono<String> responseMono = webClient.post()
                .uri(apiUrl)
                .header("X-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
        String rawResponse = responseMono.block();
        String botReplyText = "";

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            botReplyText = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText("죄송합니다, 답변을 이해할 수 없습니다."); // 기본값 설정
        } catch (Exception e) {
            log.error("JSON 응답 파싱 실패", e);
            botReplyText = "죄송합니다, 답변을 처리하는 중 오류가 발생했습니다.";
        }

        ChatbotMessage chatbotMessage = new ChatbotMessage(user, userMessage, botReplyText);

        chatbotMessageRepository.save(chatbotMessage);

        return botReplyText;
    }

    @Override
    public Slice<ChatbotMessageDto> getChatbotMessages(Long userId, Pageable pageable) {
        Slice<ChatbotMessage> messages = chatbotMessageRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(ChatbotMessageDto::new);
    }
}
