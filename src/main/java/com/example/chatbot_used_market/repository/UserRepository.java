package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.User;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET position=:position, location=:location WHERE id=:id", nativeQuery = true)
    void updatePositionAndLocationById(@Param("id") Long id, @Param("position") Point position, @Param("location") String location);
}