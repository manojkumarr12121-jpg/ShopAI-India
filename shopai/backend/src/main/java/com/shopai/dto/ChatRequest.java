package com.shopai.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChatRequest {
    private String message;
    private String context;
    private Long   userId;
    private String sessionId;
}
