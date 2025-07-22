package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Slice<Message> findByChatroomIdOrderByCreatedAtDesc(Long chatroomId, Pageable pageable);
    Optional<Message> findTopByChatroomIdOrderByCreatedAtDesc(Long chatroomId);
}
