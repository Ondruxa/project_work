package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

/**
 * Правило: пользователь имеет хотя бы один дебетовый продукт.
 * Используется как базовая характеристика клиента для построения рекомендаций.
 */
@Component
public class HasDebitProductRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public HasDebitProductRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет наличие у пользователя дебетового продукта.
     *
     * @param userId идентификатор пользователя
     * @return true если есть дебетовый продукт, иначе false
     */
    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasDebitProduct(userId);
    }
}