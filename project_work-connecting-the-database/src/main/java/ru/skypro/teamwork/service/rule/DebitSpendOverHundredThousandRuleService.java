package ru.skypro.teamwork.service.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

@Component
public class DebitSpendOverHundredThousandRuleService implements RuleService {
    private static final Logger logger = LoggerFactory.getLogger(DebitSpendOverHundredThousandRuleService.class);

    private final RecommendationsRepository repository;

    public DebitSpendOverHundredThousandRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean applyRule(UUID userId) {
        try {
            return repository.debitSpendOverHundredThousand(userId);
        } catch (Exception e) {
            logger.error("Ошибка при проверке правила для пользователя {}: {}", userId, e.getMessage(), e);
            return false;

            //return repository.debitSpendOverHundredThousand(userId);
        }
    }
}