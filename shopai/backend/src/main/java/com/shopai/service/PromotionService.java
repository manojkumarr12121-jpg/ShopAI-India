package com.shopai.service;
import com.shopai.model.Promotion;
import com.shopai.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate; import java.util.*;
@Service @RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository repo;
    public Optional<Promotion> validate(String code) {
        return repo.findByCodeAndIsActiveTrue(code).filter(p -> {
            LocalDate today = LocalDate.now();
            return (p.getStartDate()==null || !today.isBefore(p.getStartDate())) &&
                   (p.getEndDate()==null   || !today.isAfter(p.getEndDate())) &&
                   (p.getMaxUses()==null   || p.getUsedCount() < p.getMaxUses());
        });
    }
    public List<Promotion> getActive()  { return repo.findActivePromotions(LocalDate.now()); }
    public Promotion save(Promotion p)  { return repo.save(p); }
}
