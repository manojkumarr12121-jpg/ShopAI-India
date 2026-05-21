package com.shopai.repository;
import com.shopai.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart>       findByUserUserId(Long userId);
    Optional<Cart>   findByUserUserIdAndProductProductId(Long userId, Long productId);
    void             deleteByUserUserId(Long userId);
    long             countByUserUserId(Long userId);
}
