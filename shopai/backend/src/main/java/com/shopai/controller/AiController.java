package com.shopai.controller;

import com.shopai.dto.ApiResponse;
import com.shopai.dto.ChatRequest;
import com.shopai.service.AiContentService;
import com.shopai.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * AI Features REST Controller
 * /api/ai  — Chatbot, Personalisation, Behaviour Analysis, Content Generation
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final AiContentService     aiSvc;
    private final UserBehaviorService  behaviorSvc;

    // POST /api/ai/chat
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String,String>>> chat(@RequestBody ChatRequest req) {
        String response = aiSvc.generateChatbotResponse(
            req.getMessage(),
            req.getContext() != null ? req.getContext() : "General shopping assistance");
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", response, "status", "success")));
    }

    // POST /api/ai/recommend
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<Map<String,String>>> recommend(@RequestBody Map<String,Object> req) {
        @SuppressWarnings("unchecked")
        String rec = aiSvc.generatePersonalizedRecommendation(
            (String) req.get("customerName"),
            (List<String>) req.get("recentPurchases"),
            (String) req.get("recommendedProduct"),
            (String) req.get("category"));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("recommendation", rec)));
    }

    // POST /api/ai/promo-email
    @PostMapping("/promo-email")
    public ResponseEntity<ApiResponse<Map<String,String>>> promoEmail(@RequestBody Map<String,Object> req) {
        @SuppressWarnings("unchecked")
        String email = aiSvc.generatePromoEmail(
            (String) req.get("customerName"),
            (String) req.get("promoCode"),
            (Integer) req.getOrDefault("discountPercent", 20),
            (List<String>) req.getOrDefault("recommendations", List.of()));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("emailContent", email)));
    }

    // POST /api/ai/analyze-behavior
    @PostMapping("/analyze-behavior")
    public ResponseEntity<ApiResponse<Map<String,String>>> analyzeBehavior(@RequestBody Map<String,String> req) {
        String profile = aiSvc.analyzeUserBehavior(req.get("userHistory"));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("profile", profile)));
    }

    // POST /api/ai/trending-badge
    @PostMapping("/trending-badge")
    public ResponseEntity<ApiResponse<Map<String,String>>> trendingBadge(@RequestBody Map<String,Object> req) {
        String badge = aiSvc.generateTrendingHighlight(
            (String) req.get("productName"),
            (Integer) req.getOrDefault("reviews", 100),
            Double.parseDouble(req.getOrDefault("rating","4.0").toString()));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("badge", badge)));
    }

    // POST /api/ai/user-profile/{userId}
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<ApiResponse<Map<String,String>>> userProfile(@PathVariable Long userId) {
        String profile = behaviorSvc.buildAiProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("profile", profile)));
    }

    // GET /api/ai/health
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String,String>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "status", "AI Service Running",
            "model",  "GPT-3.5-turbo",
            "version","1.0.0",
            "features","Product Description, SEO Title, Chatbot, Recommendation, Comparison, Behaviour Analysis")));
    }
}
