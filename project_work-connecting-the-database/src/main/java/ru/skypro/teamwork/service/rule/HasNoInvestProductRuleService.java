package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

/**
 * Правило: у пользователя отсутствуют инвестиционные продукты.
 * Используется для предложения открытия брокерского счёта или инвестиционных сервисов.
 */
@Component
public class HasNoInvestProductRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public HasNoInvestProductRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет отсутствие у пользователя инвестиционных продуктов.
     *
     * @param userId идентификатор пользователя
     * @return true если нет инвестиционных продуктов, иначе false
     */
    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasNoInvestProduct(userId);
    }
}