package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "reviews")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id") private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;

    @Column(nullable = false) private Integer rating;
    @Column(length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String comment;
    @Column(name = "ai_summary", columnDefinition = "TEXT") private String aiSummary;
    @Column(name = "is_verified_purchase") private Boolean isVerifiedPurchase = false;
    @Column(name = "helpful_count") private Integer helpfulCount = 0;
    @Column(name = "created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
