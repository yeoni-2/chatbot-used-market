package com.example.chatbot_used_market.repository;

import com.example.chatbot_used_market.entity.Trade;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatusOrderByViewCountDesc(String status);

    // 제목에 키워드가 포함된 게시글 검색
    List<Trade> findByTitleContainingAndStatusOrderByViewCountDesc(String keyword, String status);

    // 제목에 키워드가 포함되고 특정 카테고리인 게시글 검색
    List<Trade> findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(String keyword, String category, String status);

    // 페이지네이션을 지원하는 검색 메소드들
    Page<Trade> findByTitleContainingAndStatusOrderByViewCountDesc(String keyword, String status, Pageable pageable);
    Page<Trade> findByTitleContainingAndCategoryAndStatusOrderByViewCountDesc(String keyword, String category, String status, Pageable pageable);

    Page<Trade> findByStatus(String status, Pageable pageable);

    @Query(value = "SELECT * FROM trades WHERE (seller_id=:userId OR buyer_id=:userId) AND status=:status", nativeQuery = true)
    List<Trade> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // 위치 기반 조회 메서드들 (5km 반경)
    @Query(value = "SELECT t.* FROM trades t " +
            "JOIN users u ON t.seller_id = u.id " +
            "WHERE t.status = :status " +
            "AND u.position IS NOT NULL " +
            "AND ST_DWithin(u.position, :userPosition, 5000) " +
            "ORDER BY t.view_count DESC",
            nativeQuery = true)
    List<Trade> findNearbyTradesOrderByViewCountDesc(@Param("userPosition") Point userPosition,
                                                     @Param("status") String status);

    @Query(value = "SELECT t.* FROM trades t " +
            "JOIN users u ON t.seller_id = u.id " +
            "WHERE t.status = :status " +
            "AND u.position IS NOT NULL " +
            "AND ST_DWithin(u.position, :userPosition, 5000) " +
            "ORDER BY t.view_count DESC",
            countQuery = "SELECT count(t.*) FROM trades t " +
                        "JOIN users u ON t.seller_id = u.id " +
                        "WHERE t.status = :status " +
                        "AND u.position IS NOT NULL " +
                        "AND ST_DWithin(u.position, :userPosition, 5000)",
            nativeQuery = true)
    Page<Trade> findNearbyTradesOrderByViewCountDesc(@Param("userPosition") Point userPosition,
                                                     @Param("status") String status,
                                                     Pageable pageable);

    // 키워드 검색 + 위치 기반 조회
    @Query(value = "SELECT t.* FROM trades t " +
            "JOIN users u ON t.seller_id = u.id " +
            "WHERE t.title LIKE CONCAT('%', :keyword, '%') " +
            "AND t.status = :status " +
            "AND u.position IS NOT NULL " +
            "AND ST_DWithin(u.position, :userPosition, 5000) " +
            "ORDER BY t.view_count DESC",
            countQuery = "SELECT count(t.*) FROM trades t " +
                        "JOIN users u ON t.seller_id = u.id " +
                        "WHERE t.title LIKE CONCAT('%', :keyword, '%') " +
                        "AND t.status = :status " +
                        "AND u.position IS NOT NULL " +
                        "AND ST_DWithin(u.position, :userPosition, 5000)",
            nativeQuery = true)
    Page<Trade> findNearbyTradesByKeywordOrderByViewCountDesc(@Param("keyword") String keyword,
                                                              @Param("userPosition") Point userPosition,
                                                              @Param("status") String status,
                                                              Pageable pageable);

    // 키워드 + 카테고리 검색 + 위치 기반 조회
    @Query(value = "SELECT t.* FROM trades t " +
            "JOIN users u ON t.seller_id = u.id " +
            "WHERE t.title LIKE CONCAT('%', :keyword, '%') " +
            "AND t.category = :category " +
            "AND t.status = :status " +
            "AND u.position IS NOT NULL " +
            "AND ST_DWithin(u.position, :userPosition, 5000) " +
            "ORDER BY t.view_count DESC",
            countQuery = "SELECT count(t.*) FROM trades t " +
                        "JOIN users u ON t.seller_id = u.id " +
                        "WHERE t.title LIKE CONCAT('%', :keyword, '%') " +
                        "AND t.category = :category " +
                        "AND t.status = :status " +
                        "AND u.position IS NOT NULL " +
                        "AND ST_DWithin(u.position, :userPosition, 5000)",
            nativeQuery = true)
    Page<Trade> findNearbyTradesByKeywordAndCategoryOrderByViewCountDesc(@Param("keyword") String keyword,
                                                                         @Param("category") String category,
                                                                         @Param("userPosition") Point userPosition,
                                                                         @Param("status") String status,
                                                                         Pageable pageable);
}
