package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

@Component
public class HasNoCreditProductRuleService implements RuleService {
    private final RecommendationsRepository repository;

    public HasNoCreditProductRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasNoCreditProduct(userId);
    }
}