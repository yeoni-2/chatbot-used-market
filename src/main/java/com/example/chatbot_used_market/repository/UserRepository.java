package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET position=ST_GeomFromText(:point, 4326) WHERE id=:id", nativeQuery = true)
    void updatePositionById(@Param("id") Long id, @Param("point") String point);
}