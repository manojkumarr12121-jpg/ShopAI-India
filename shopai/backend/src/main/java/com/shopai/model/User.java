package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 15) private String phone;
    @Column(name = "avatar_url") private String avatarUrl;
    @Column(length = 100) private String city;
    @Column(length = 100) private String state;
    @Column(length = 10)  private String pincode;
    @Column(columnDefinition = "TEXT") private String address;
    @Column(name = "loyalty_points") @Builder.Default private Integer loyaltyPoints = 0;
    @Column(name = "is_active") @Builder.Default private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) @Builder.Default
    private Role role = Role.CUSTOMER;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Role { CUSTOMER, ADMIN, SELLER }
}
