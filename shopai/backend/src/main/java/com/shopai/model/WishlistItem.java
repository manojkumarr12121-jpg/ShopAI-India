package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","product_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id") private Long cartId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Builder.Default private Integer quantity = 1;
    @Column(name = "added_at") private LocalDateTime addedAt;
    @PrePersist protected void onCreate() { addedAt = LocalDateTime.now(); }
}

@Entity
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","product_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WishlistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id") private Long wishlistId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(name = "added_at") private LocalDateTime addedAt;
    @PrePersist protected void onCreate() { addedAt = LocalDateTime.now(); }
}

@Entity
@Table(name = "chatbot_conversations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
class ChatbotConversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conv_id") private Long convId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") private User user;
    @Column(name = "session_id", nullable = false, length = 100) private String sessionId;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(columnDefinition = "TEXT") private String response;
    @Column(length = 100) private String intent;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}

@Entity
@Table(name = "ai_content_log")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiContentLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id") private Long logId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") private Product product;
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 30) private ContentType contentType;
    @Column(name = "generated_content", columnDefinition = "TEXT") private String generatedContent;
    @Column(name = "prompt_used", columnDefinition = "TEXT") private String promptUsed;
    @Column(name = "model_used", length = 100) private String modelUsed;
    @Column(name = "tokens_used") private Integer tokensUsed;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public enum ContentType { TITLE, DESCRIPTION, SEO, EMAIL, PROMO, COMPARISON, HIGHLIGHT }
}
