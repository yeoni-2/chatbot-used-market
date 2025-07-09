package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // chatroom의 id를 기준으로 모든 message를 생성 시간 오름차순으로 조회
    List<Message> findByChatroomIdOrderByCreatedAtAsc(Long chatroomId);
}
