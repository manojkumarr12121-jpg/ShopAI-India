package com.shopai.controller;

import com.shopai.dto.ApiResponse;
import com.shopai.model.Promotion;
import com.shopai.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromotionController {

    private final PromotionService promoSvc;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Promotion>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(promoSvc.getActive()));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String,Object>>> validate(@RequestBody Map<String,String> req) {
        return promoSvc.validate(req.get("code")).map(p -> {
            Map<String,Object> result = new HashMap<>();
            result.put("valid",         true);
            result.put("discountType",  p.getDiscountType());
            result.put("discountValue", p.getDiscountValue());
            result.put("title",         p.getTitle());
            result.put("message",       p.getAiGeneratedContent());
            return ResponseEntity.ok(ApiResponse.ok("Promo code valid", result));
        }).orElse(ResponseEntity.ok(ApiResponse.error("Invalid or expired promo code")));
    }
}
