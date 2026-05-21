package com.shopai.service;

import com.shopai.model.Product;
import com.shopai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class ProductService {

    private final ProductRepository productRepo;
    private final AiContentService  aiSvc;

    @Cacheable("trending")
    public List<Product> getTrending(int limit) {
        return productRepo.findByIsTrendingTrueAndIsActiveTrueOrderByReviewCountDesc(PageRequest.of(0, limit));
    }
    @Cacheable("featured")
    public List<Product> getFeatured(int limit) {
        return productRepo.findByIsFeaturedTrueAndIsActiveTrueOrderByRatingDesc(PageRequest.of(0, limit));
    }
    public List<Product> getDeals(int limit) {
        return productRepo.findDeals(new BigDecimal("10"), PageRequest.of(0, limit));
    }
    public List<Product> getTopRated(int limit)    { return productRepo.findTopRated(PageRequest.of(0, limit)); }
    public List<Product> getNewArrivals(int limit) { return productRepo.findNewArrivals(PageRequest.of(0, limit)); }

    public Page<Product> search(String kw, int page, int size) {
        return productRepo.searchProducts(kw, PageRequest.of(page, size));
    }
    public Optional<Product> getById(Long id)      { return productRepo.findById(id); }
    public List<Product> getByCategory(Long catId) {
        return productRepo.findByCategoryCategoryIdAndIsActiveTrueOrderByRatingDesc(catId);
    }
    public List<Product> getSimilar(Long catId, Long pid, int limit) {
        return productRepo.findSimilarProducts(catId, pid, PageRequest.of(0, limit));
    }
    public List<Product> getByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepo.findByPriceRange(min, max);
    }

    @Transactional
    public Product createWithAiContent(Product product) {
        String catName = product.getCategory() != null ? product.getCategory().getName() : "General";
        String brand   = product.getBrand() != null ? product.getBrand() : "Brand";

        product.setAiGeneratedDescription(
            aiSvc.generateProductDescription(product.getTitle(), catName, brand, product.getPrice().doubleValue()));
        product.setAiGeneratedTitle(
            aiSvc.generateSeoTitle(product.getTitle(), catName, brand));
        product.setAiSeoDescription(
            aiSvc.generateSeoDescription(product.getTitle(), catName, brand, ""));
        log.info("AI content generated for: {}", product.getTitle());
        return productRepo.save(product);
    }

    @Transactional
    public Product update(Long id, Product upd) {
        return productRepo.findById(id).map(p -> {
            p.setTitle(upd.getTitle());
            p.setDescription(upd.getDescription());
            p.setPrice(upd.getPrice());
            p.setDiscountPercent(upd.getDiscountPercent());
            p.setStockQty(upd.getStockQty());
            p.setBrand(upd.getBrand());
            p.setImageUrl(upd.getImageUrl());
            p.setIsTrending(upd.getIsTrending());
            p.setIsFeatured(upd.getIsFeatured());
            return productRepo.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Transactional
    public void delete(Long id) {
        productRepo.findById(id).ifPresent(p -> {
            p.setIsActive(false);
            productRepo.save(p);
        });
    }
    public long count() { return productRepo.count(); }
}
