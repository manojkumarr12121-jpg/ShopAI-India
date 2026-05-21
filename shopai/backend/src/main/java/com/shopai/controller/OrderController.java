package com.shopai.controller;

import com.shopai.dto.ApiResponse;
import com.shopai.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders/place
    // Body: { userId, cartItems: [{productId, qty, finalPrice}], paymentMethod, shippingAddress, promoCode }
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<Map<String, Object>>> placeOrder(
            @RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = orderService.placeOrder(body);
            return ResponseEntity.ok(ApiResponse.ok("Order placed successfully!", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/orders/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrdersByUser(
            @PathVariable Long userId) {
        try {
            List<Map<String, Object>> orders = orderService.getOrdersByUser(userId);
            return ResponseEntity.ok(ApiResponse.ok(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/orders/{orderNumber}
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderDetail(
            @PathVariable String orderNumber) {
        try {
            Map<String, Object> order = orderService.getOrderDetail(orderNumber);
            return ResponseEntity.ok(ApiResponse.ok(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
