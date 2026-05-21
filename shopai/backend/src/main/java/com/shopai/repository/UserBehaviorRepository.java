package com.shopai.repository;
import com.shopai.model.UserBehavior;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT ub.product.productId, COUNT(ub) AS cnt FROM UserBehavior ub " +
           "WHERE ub.actionType IN ('VIEW','ADD_CART','PURCHASE') " +
           "GROUP BY ub.product.productId ORDER BY cnt DESC")
    List<Object[]> findTrendingProductIds(Pageable p);
}
