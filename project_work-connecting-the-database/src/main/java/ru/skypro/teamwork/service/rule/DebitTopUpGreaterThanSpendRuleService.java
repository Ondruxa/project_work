package ru.skypro.teamwork.service.rule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.UUID;


@Component
public class DebitTopUpGreaterThanSpendRuleService implements RuleService {
    private static final Logger logger = LoggerFactory.getLogger(DebitTopUpGreaterThanSpendRuleService.class);
    private final RecommendationsRepository repository;

    public DebitTopUpGreaterThanSpendRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean applyRule(UUID userId) {
        try {
            return repository.debitTopUpGreaterThanDebitSpend(userId);
        } catch (Exception e) {
            logger.error("Ошибка при проверке пополнения/расходов пользователя {}: {}",
                    userId, e.getMessage(), e);
            return false;
            //return repository.debitTopUpGreaterThanDebitSpend(userId);
        }
    }
}