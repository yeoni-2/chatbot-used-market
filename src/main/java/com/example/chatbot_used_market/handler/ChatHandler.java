package com.example.chatbot_used_market.handler;

import com.example.chatbot_used_market.dto.MessageDto;
import com.example.chatbot_used_market.dto.WebSocketMessage;
import com.example.chatbot_used_market.entity.Message;
import com.example.chatbot_used_market.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    // key: roomId, value: set of sessions in that room
    private final Map<Long, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);
        WebSocketMessage webSocketMessage = objectMapper.readValue(payload, WebSocketMessage.class);

        Long chatroomId = webSocketMessage.getChatroomId();

        Long currentUserId = (Long) session.getAttributes().get("loginUserId");
        if (currentUserId == null) return;

        if (webSocketMessage.getType() == WebSocketMessage.MessageType.JOIN) {
            Set<WebSocketSession> sessions = chatRooms.computeIfAbsent(chatroomId, key -> ConcurrentHashMap.newKeySet());
            sessions.add(session);
            log.info("Joined room: chatroomId={}, session={}", chatroomId, session.getId());

            chatService.markMessagesAsRead(chatroomId, currentUserId);
        }

        if (webSocketMessage.getType() == WebSocketMessage.MessageType.TALK) {
            Message savedMessage = chatService.createMessage(chatroomId, currentUserId, webSocketMessage.getContent());
            MessageDto messageDto = new MessageDto(savedMessage);

            Set<WebSocketSession> sessions = chatRooms.get(chatroomId);
            if (sessions != null) {
                sessions.parallelStream().forEach(s -> sendMessage(s, messageDto));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 클라이언트 접속", session.getId());
        // 실제 구현에서는 여기서 어떤 채팅방에 들어갈지 구분하는 로직이 필요합니다.
        // 예: long roomId = extractRoomIdFromSession(session);
        // chatRooms.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 클라이언트 접속 해제", session.getId());
        chatRooms.values().forEach(sessions -> sessions.remove(session));
    }

    // 메시지 발송 로직
    private void sendMessage(WebSocketSession session, MessageDto message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            log.error("메시지 발송 오류", e);
        }
    }
}
