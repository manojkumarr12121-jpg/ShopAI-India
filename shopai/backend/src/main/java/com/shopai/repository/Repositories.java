package com.shopai.repository;

import com.shopai.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserUserId(Long userId);
    boolean existsByUserUserIdAndProductProductId(Long userId, Long productId);
    void deleteByUserUserIdAndProductProductId(Long userId, Long productId);
}
