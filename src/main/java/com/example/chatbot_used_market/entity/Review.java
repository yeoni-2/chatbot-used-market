package com.example.chatbot_used_market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "reviewer_id")
  private User reviewer;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "reviewee_id")
  private User reviewee;

  @ManyToOne
  @NotNull
  @JoinColumn(name = "trade_id")
  private Trade trade;

  @NotNull
  @Column(name = "rating")
  private Integer rating;

  @Column(name = "content")
  private String content;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public Review(){}

  public Review(User reviewer, User reviewee, Trade trade, Integer rating, String content){
    this.reviewer = reviewer;
    this.reviewee = reviewee;
    this.trade = trade;
    this.rating = rating;
    this.content = content;
    this.createdAt = LocalDateTime.now();
  }
}
