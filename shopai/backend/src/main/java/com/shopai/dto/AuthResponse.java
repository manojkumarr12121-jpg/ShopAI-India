package com.shopai.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    @Builder.Default private String type = "Bearer";
    private Long   userId;
    private String email;
    private String fullName;
    private String message;
}
