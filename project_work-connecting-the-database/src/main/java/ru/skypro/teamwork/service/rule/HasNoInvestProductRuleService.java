package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

@Component
public class HasNoInvestProductRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public HasNoInvestProductRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasNoInvestProduct(userId);
    }
}