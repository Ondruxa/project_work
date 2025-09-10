package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

/**
 * Правило: пользователь пополнял накопительный продукт суммарно более чем на 1 000.
 * Используется для выявления активности накоплений и формирования дополнительных предложений.
 */
@Component
public class SavingTopUpOverThousandRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public SavingTopUpOverThousandRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет, превышает ли сумма пополнений накопительных продуктов порог 1 000.
     *
     * @param userId идентификатор пользователя
     * @return true если сумма пополнений больше 1 000, иначе false
     */
    @Override
    public boolean applyRule(UUID userId) {
        return repository.userSavingTopUpOverThousand(userId);
    }
}