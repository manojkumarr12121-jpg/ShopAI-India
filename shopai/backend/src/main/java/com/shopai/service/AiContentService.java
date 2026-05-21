package com.shopai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AI Content Generation Service — Proposed System Core
 *
 * Capabilities:
 *  1. Personalized product descriptions (Indian market)
 *  2. SEO-optimized product titles
 *  3. Marketing / promotional email content
 *  4. NLP-powered chatbot responses
 *  5. Product comparison summaries
 *  6. User behaviour analysis + JSON profiling
 *  7. Trending badge text
 *  8. SEO meta descriptions
 */
@Service
@Slf4j
public class AiContentService {

    @Value("${ai.api.url}")         private String aiApiUrl;
    @Value("${ai.api.key}")         private String aiApiKey;
    @Value("${ai.model:gpt-3.5-turbo}") private String aiModel;
    @Value("${ai.max.tokens:600}")  private int    maxTokens;

    private final WebClient    webClient;
    private final ObjectMapper objectMapper;

    public AiContentService(WebClient.Builder wcb, ObjectMapper om) {
        this.webClient    = wcb.build();
        this.objectMapper = om;
    }

    /* 1 ── Product Description */
    public String generateProductDescription(String name, String category, String brand, double price) {
        return callAi(String.format(
            "You are an expert e-commerce copywriter for India. Write a compelling " +
            "3-sentence product description for '%s' by %s in '%s', priced at Rs%.2f. " +
            "Focus on Indian customer needs, value for money and trust. Be honest and enthusiastic.",
            name, brand, category, price));
    }

    /* 2 ── SEO Title */
    public String generateSeoTitle(String name, String category, String brand) {
        return callAi(String.format(
            "Create an SEO product title (max 80 chars) for Indian e-commerce. " +
            "Product='%s', Brand='%s', Category='%s'. " +
            "Format: ProductName - Key Benefit | Brand India", name, brand, category));
    }

    /* 3 ── Personalised Recommendation */
    public String generatePersonalizedRecommendation(String customerName,
            List<String> recentPurchases, String product, String category) {
        return callAi(String.format(
            "AI shopping assistant for India. Customer %s recently bought: %s. " +
            "Write a warm 2-sentence recommendation for '%s' (%s). " +
            "Use friendly Indian English.", customerName,
            String.join(", ", recentPurchases), product, category));
    }

    /* 4 ── Promo Email */
    public String generatePromoEmail(String customerName, String code, int pct, List<String> recs) {
        return callAi(String.format(
            "Write a short promo email (under 120 words) for Indian customer %s. " +
            "Code: %s for %d%% off. Products: %s. Create urgency, festive warmth, clear CTA.",
            customerName, code, pct, String.join(", ", recs)));
    }

    /* 5 ── Chatbot (NLP) */
    public String generateChatbotResponse(String userMessage, String context) {
        return callAi(String.format(
            "You are a customer-service AI for ShopAI India. Context: %s\n" +
            "Customer: '%s'\nRespond helpfully in 2-3 sentences using friendly Indian English.",
            context, userMessage));
    }

    /* 6 ── Product Comparison */
    public String generateComparisonSummary(String p1, String p2, Map<String,String> specs) {
        return callAi(String.format(
            "Compare '%s' vs '%s' for Indian buyers. Specs: %s. " +
            "3 sentences: budget pick, premium pick, overall Indian value.",
            p1, p2, specs));
    }

    /* 7 ── Behaviour Analysis */
    public String analyzeUserBehavior(String history) {
        return callAi(String.format(
            "Indian shopper history: %s\n" +
            "Return ONLY valid JSON with keys: preferredCategories (array), " +
            "priceRangeMin (INR), priceRangeMax (INR), topInterests (array). No extra text.",
            history));
    }

    /* 8 ── Trending Badge */
    public String generateTrendingHighlight(String name, int reviews, double rating) {
        return callAi(String.format(
            "One-line trending badge (under 55 chars) for '%s' with %d reviews and %.1f/5. Be exciting.",
            name, reviews, rating));
    }

    /* 9 ── SEO Meta Description */
    public String generateSeoDescription(String name, String category, String brand, String features) {
        return callAi(String.format(
            "SEO meta description (under 160 chars) for '%s' by %s in %s. Features: %s. " +
            "Include: India, Buy Online.", name, brand, category, features));
    }

    // ─────────────────────────────────────────────────────────
    private String callAi(String prompt) {
    try {
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        String url = aiApiUrl + "?key=" + aiApiKey;

        String raw = webClient.post()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        if (raw != null) {
            return objectMapper.readTree(raw)
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText();
        }
    } catch (Exception e) {
        log.warn("AI API failed: {}", e.getMessage());
    }
    return fallback(prompt);
}


   private String fallback(String p) {
    String l = p.toLowerCase();
    if (l.contains("description")) return "Premium quality product trusted by millions across India. Outstanding value and superior build quality make it the top choice in its category.";
    if (l.contains("title"))       return "Premium Product - Best Quality & Value | Top Brand India";
    if (l.contains("promo"))       return "Exciting deals just for you! Use your code for exclusive savings. Shop now before the offer expires!";
    if (l.contains("compare"))     return "Both are excellent choices. For budget-conscious Indian buyers option one offers better value; premium buyers will prefer option two for enhanced features.";
    if (l.contains("customer"))    return "Namaste! I am here to help you find the best products and deals. What are you looking for today?";
    return "Great choice for Indian shoppers. Enjoy fast pan-India delivery and easy returns.";
}
    @Async
    public CompletableFuture<String> generateAsync(String prompt) {
        return CompletableFuture.completedFuture(callAi(prompt));
    }
}
