package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notif_id") private Long notifId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;

    @Column(length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String message;

    @Enumerated(EnumType.STRING) private NotifType type = NotifType.SYSTEM;
    @Column(name = "is_read") private Boolean isRead = false;
    @Column(name = "created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public enum NotifType { PROMO, ORDER, RESTOCK, AI_SUGGESTION, SYSTEM }
}
