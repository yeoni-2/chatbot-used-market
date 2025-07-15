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
  @JoinColumn(name = "reviewer")
  private User reviewer;

  @ManyToOne
  @NotNull
  @JoinColumn(name = "reviewee")
  private User reviewee;

  @ManyToOne
  @NotNull
  private Trade trade;

  @NotNull
  private Integer rating;

  private String content;
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
