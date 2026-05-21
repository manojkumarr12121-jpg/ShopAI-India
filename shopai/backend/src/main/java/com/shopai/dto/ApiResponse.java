package com.shopai.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T d) {
        return new ApiResponse<>(true, "Success", d);
    }
    public static <T> ApiResponse<T> ok(String m, T d) {
        return new ApiResponse<>(true, m, d);
    }
    public static <T> ApiResponse<T> error(String m) {
        return new ApiResponse<>(false, m, null);
    }
}
