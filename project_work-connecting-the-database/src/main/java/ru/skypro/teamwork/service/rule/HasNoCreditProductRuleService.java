package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

/**
 * Правило: у пользователя отсутствуют активные кредитные продукты.
 * Может использоваться для формирования предложений по кредитам.
 */
@Component
public class HasNoCreditProductRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public HasNoCreditProductRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет отсутствие у пользователя кредитных продуктов.
     *
     * @param userId идентификатор пользователя
     * @return true если кредитных продуктов нет, иначе false
     */
    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasNoCreditProduct(userId);
    }
}