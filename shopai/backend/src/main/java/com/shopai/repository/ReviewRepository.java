package com.shopai.repository;
import com.shopai.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductProductIdOrderByCreatedAtDesc(Long productId);
    List<Review> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId=:pid")
    Double avgRatingByProduct(@Param("pid") Long productId);
    long countByProductProductId(Long productId);
}
