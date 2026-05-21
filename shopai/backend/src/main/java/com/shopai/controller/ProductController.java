package com.shopai.controller;

import com.shopai.dto.ApiResponse;
import com.shopai.model.Product;
import com.shopai.service.AiContentService;
import com.shopai.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService   productSvc;
    private final AiContentService aiSvc;

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<Product>>> getTrending(@RequestParam(defaultValue="8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getTrending(limit)));
    }
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeatured(@RequestParam(defaultValue="8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getFeatured(limit)));
    }
    @GetMapping("/deals")
    public ResponseEntity<ApiResponse<List<Product>>> getDeals(@RequestParam(defaultValue="8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getDeals(limit)));
    }
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<Product>>> getTopRated(@RequestParam(defaultValue="8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getTopRated(limit)));
    }
    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<List<Product>>> getNewArrivals(@RequestParam(defaultValue="8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getNewArrivals(limit)));
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.search(keyword, page, size)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long id) {
        return productSvc.getById(id)
            .map(p -> ResponseEntity.ok(ApiResponse.ok(p)))
            .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getByCategory(categoryId)));
    }
    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<List<Product>>> getSimilar(@PathVariable Long id) {
        return productSvc.getById(id).map(p -> {
            Long catId = p.getCategory() != null ? p.getCategory().getCategoryId() : null;
            return ResponseEntity.ok(ApiResponse.ok(productSvc.getSimilar(catId, id, 6)));
        }).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<Product>>> getByPriceRange(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.ok(productSvc.getByPriceRange(min, max)));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.ok("Product created", productSvc.createWithAiContent(product)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.ok("Product updated", productSvc.update(id, product)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        productSvc.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deactivated", null));
    }
    @PostMapping("/ai/generate-description")
    public ResponseEntity<ApiResponse<Map<String,String>>> generateDesc(@RequestBody Map<String,String> req) {
        String desc = aiSvc.generateProductDescription(
            req.get("name"), req.getOrDefault("category","General"),
            req.getOrDefault("brand","Brand"),
            Double.parseDouble(req.getOrDefault("price","0")));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("description", desc)));
    }
    @PostMapping("/ai/generate-title")
    public ResponseEntity<ApiResponse<Map<String,String>>> generateTitle(@RequestBody Map<String,String> req) {
        String title = aiSvc.generateSeoTitle(req.get("name"),
            req.getOrDefault("category","General"), req.getOrDefault("brand","Brand"));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("title", title)));
    }
    @PostMapping("/ai/compare")
    public ResponseEntity<ApiResponse<Map<String,String>>> compare(@RequestBody Map<String,Object> req) {
        String cmp = aiSvc.generateComparisonSummary(
            (String)req.get("product1"), (String)req.get("product2"), new HashMap<>());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("comparison", cmp)));
    }
}
