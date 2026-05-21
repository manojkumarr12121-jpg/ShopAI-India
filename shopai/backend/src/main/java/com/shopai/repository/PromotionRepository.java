package com.shopai.repository;
import com.shopai.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate; import java.util.*;
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCodeAndIsActiveTrue(String code);
    @Query("SELECT p FROM Promotion p WHERE p.isActive=true AND p.startDate<=:today AND p.endDate>=:today")
    List<Promotion> findActivePromotions(LocalDate today);
}
