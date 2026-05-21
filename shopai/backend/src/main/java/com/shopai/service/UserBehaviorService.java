package com.shopai.service;

import com.shopai.model.UserBehavior;
import com.shopai.repository.UserBehaviorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class UserBehaviorService {

    private final UserBehaviorRepository repo;
    private final AiContentService aiSvc;

    @Async
    public void track(UserBehavior b) {
        try { repo.save(b); } catch (Exception e) { log.warn("Behavior track: {}", e.getMessage()); }
    }

    public List<UserBehavior> getHistory(Long userId) {
        return repo.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    public String buildAiProfile(Long userId) {
        List<UserBehavior> hist = getHistory(userId);
        StringBuilder sb = new StringBuilder();
        hist.stream().limit(20).forEach(b -> {
            String action = b.getActionType() != null ? b.getActionType().name() : "VIEW";
            String product = (b.getProduct() != null) ? b.getProduct().getTitle() : "unknown";
            sb.append(action).append(" on ").append(product).append(", ");
        });
        return aiSvc.analyzeUserBehavior(sb.toString());
    }
}
