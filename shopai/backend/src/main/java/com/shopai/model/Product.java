package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255) private String title;
    @Column(name = "ai_generated_title",   length = 300) private String aiGeneratedTitle;
    @Column(columnDefinition = "TEXT")                   private String description;
    @Column(name = "ai_generated_description", columnDefinition = "TEXT") private String aiGeneratedDescription;
    @Column(name = "ai_seo_description",        columnDefinition = "TEXT") private String aiSeoDescription;

    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "stock_qty") private Integer stockQty = 0;
    @Column(unique = true, length = 100) private String sku;
    @Column(length = 100) private String brand;
    @Column(name = "image_url", length = 500) private String imageUrl;

    @Column(precision = 3, scale = 2) private BigDecimal rating = BigDecimal.ZERO;
    @Column(name = "review_count")    private Integer reviewCount = 0;
    @Column(name = "is_trending")     private Boolean isTrending  = false;
    @Column(name = "is_featured")     private Boolean isFeatured  = false;
    @Column(name = "is_active")       private Boolean isActive    = true;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Transient
    public BigDecimal getFinalPrice() {
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal disc = price.multiply(discountPercent)
                                   .divide(new BigDecimal("100"));
            return price.subtract(disc).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return price;
    }
}
