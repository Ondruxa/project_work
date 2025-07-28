package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

@Component
public class DebitTopUpGreaterThanSpendRule implements Rule {
    private final RecommendationsRepository repository;

    public DebitTopUpGreaterThanSpendRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean applyRule(UUID userId) {
        return repository.debitTopUpGreaterThanDebitSpend(userId);
    }
}