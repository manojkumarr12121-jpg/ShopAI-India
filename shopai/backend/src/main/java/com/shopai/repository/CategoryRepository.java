package com.shopai.repository;
import com.shopai.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrueOrderByName();
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
