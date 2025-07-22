package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Slice<Message> findByChatroomIdOrderByCreatedAtDesc(Long chatroomId, Pageable pageable);
    Optional<Message> findTopByChatroomIdOrderByCreatedAtDesc(Long chatroomId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chatroom.id = :chatroomId AND m.sender.id <> :userId AND m.isRead = false")
    void markAsReadByChatroomAndUser(@Param("chatroomId") Long chatroomId, @Param("userId") Long userId);

    @Query("SELECT count(m) FROM Message m WHERE (m.chatroom.buyer.id = :userId OR m.chatroom.seller.id = :userId) AND m.sender.id <> :userId AND m.isRead = false")
    long countUnreadMessagesByUserId(@Param("userId") Long userId);
}
