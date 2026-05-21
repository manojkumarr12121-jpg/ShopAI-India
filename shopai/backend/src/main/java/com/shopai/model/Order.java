package com.shopai.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id") private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;

    @Column(name = "order_number", unique = true, length = 50) private String orderNumber;
    @Column(name = "total_amount",   precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(name = "discount_amount",precision = 12, scale = 2) private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(name = "shipping_amount",precision = 8,  scale = 2) private BigDecimal shippingAmount = BigDecimal.ZERO;
    @Column(name = "final_amount",   precision = 12, scale = 2) private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "payment_method", length = 50) private String paymentMethod;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "shipping_address", columnDefinition = "TEXT") private String shippingAddress;
    @Column(name = "tracking_number",  length = 100) private String trackingNumber;
    @Column(name = "promo_code",       length = 50)  private String promoCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum OrderStatus  { PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED }
    public enum PaymentStatus { PENDING, PAID, FAILED, REFUNDED }
}
