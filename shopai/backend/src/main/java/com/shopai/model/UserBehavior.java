package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "user_behavior")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserBehavior {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "behavior_id") private Long behaviorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") private User user;

    @Column(name = "session_id", length = 100) private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false) private ActionType actionType;

    @Column(name = "search_query", length = 255) private String searchQuery;
    @Column(name = "duration_seconds") private Integer durationSeconds;
    @Column(name = "device_type", length = 50) private String deviceType;
    @Column(name = "created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ActionType { VIEW, SEARCH, ADD_CART, PURCHASE, WISHLIST, COMPARE, REVIEW }
}
