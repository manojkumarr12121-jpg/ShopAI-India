package com.shopai.repository;

import com.shopai.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsTrendingTrueAndIsActiveTrueOrderByReviewCountDesc(Pageable p);
    List<Product> findByIsFeaturedTrueAndIsActiveTrueOrderByRatingDesc(Pageable p);
    List<Product> findByCategoryCategoryIdAndIsActiveTrueOrderByRatingDesc(Long categoryId);
    List<Product> findByBrandIgnoreCaseAndIsActiveTrueOrderByRatingDesc(String brand);

    @Query("SELECT p FROM Product p WHERE p.isActive=true AND (" +
           "LOWER(p.title) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(p.aiGeneratedDescription) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%',:kw,'%')))")
    Page<Product> searchProducts(@Param("kw") String kw, Pageable p);

    @Query("SELECT p FROM Product p WHERE p.isActive=true AND p.category.categoryId=:cid AND p.productId!=:pid ORDER BY p.rating DESC")
    List<Product> findSimilarProducts(@Param("cid") Long cid, @Param("pid") Long pid, Pageable p);

    @Query("SELECT p FROM Product p WHERE p.isActive=true ORDER BY p.rating DESC, p.reviewCount DESC")
    List<Product> findTopRated(Pageable p);

    @Query("SELECT p FROM Product p WHERE p.isActive=true AND p.discountPercent>=:minDisc ORDER BY p.discountPercent DESC")
    List<Product> findDeals(@Param("minDisc") BigDecimal minDisc, Pageable p);

    @Query("SELECT p FROM Product p WHERE p.isActive=true AND p.price BETWEEN :minP AND :maxP ORDER BY p.rating DESC")
    List<Product> findByPriceRange(@Param("minP") BigDecimal min, @Param("maxP") BigDecimal max);

    @Query("SELECT p FROM Product p WHERE p.isActive=true ORDER BY p.createdAt DESC")
    List<Product> findNewArrivals(Pageable p);

    @Query("SELECT p.category.name, COUNT(p) FROM Product p WHERE p.isActive=true GROUP BY p.category.name")
    List<Object[]> countByCategory();
}
