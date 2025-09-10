package ru.skypro.teamwork.service.rule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.UUID;

/**
 * Правило: общая сумма пополнений на дебетовые продукты больше общей суммы расходов.
 * Используется для выявления пользователей с положительным денежным потоком
 * по дебетовым операциям, что может влиять на рекомендации (вклады, инвестиции и т.п.).
 */
@Component
public class DebitTopUpGreaterThanSpendRuleService implements RuleService {

    private static final Logger logger = LoggerFactory.getLogger(DebitTopUpGreaterThanSpendRuleService.class);
    private final RecommendationsRepository repository;

    public DebitTopUpGreaterThanSpendRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет, превышает ли сумма пополнений сумму расходов для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return true если пополнения больше расходов; false при отсутствии данных или ошибке
     */
    @Override
    public boolean applyRule(UUID userId) {
        try {
            return repository.debitTopUpGreaterThanDebitSpend(userId);
        } catch (Exception e) {
            logger.error("Ошибка при проверке пополнения/расходов пользователя {}: {}",
                    userId, e.getMessage(), e);
            return false;
        }
    }
}