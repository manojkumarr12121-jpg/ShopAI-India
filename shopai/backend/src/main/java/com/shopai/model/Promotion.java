package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "promotions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Promotion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_id") private Long promoId;

    @Column(unique = true, nullable = false, length = 50) private String code;
    @Column(length = 200) private String title;
    @Column(name = "ai_generated_content", columnDefinition = "TEXT") private String aiGeneratedContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false) private DiscountType discountType;

    @Column(name = "discount_value",    precision = 10, scale = 2) private BigDecimal discountValue;
    @Column(name = "min_order_amount",  precision = 10, scale = 2) private BigDecimal minOrderAmount = BigDecimal.ZERO;
    @Column(name = "max_uses")    private Integer maxUses;
    @Column(name = "used_count")  private Integer usedCount = 0;
    @Column(name = "start_date")  private LocalDate startDate;
    @Column(name = "end_date")    private LocalDate endDate;
    @Column(name = "is_active")   private Boolean isActive = true;

    public enum DiscountType { PERCENT, FLAT }
}
