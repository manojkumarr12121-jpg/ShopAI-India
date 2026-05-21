package com.shopai.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiChatRequest {
    private String message;
    private String context;
    private String sessionId;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiChatResponse {
    private String message;
    private String intent;
    private String sessionId;
    private String status;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiContentRequest {
    private String productName;
    private String category;
    private String brand;
    private Double price;
    private String contentType;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiContentResponse {
    private String content;
    private String contentType;
    private String model;
    private Boolean fromFallback;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiRecommendRequest {
    private String customerName;
    private List<String> recentPurchases;
    private String recommendedProduct;
    private String category;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class AiPromoEmailRequest {
    private String customerName;
    private String promoCode;
    private Integer discountPercent;
    private List<String> recommendations;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
class OrderCreateRequest {
    private List<OrderItemRequest> items;
    private String shippingAddress;
    private String paymentMethod;
    private String promoCode;
}
