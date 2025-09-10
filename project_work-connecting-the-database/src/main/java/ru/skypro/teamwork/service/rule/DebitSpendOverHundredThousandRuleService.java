package ru.skypro.teamwork.service.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import java.util.UUID;

/**
 * Правило: сумма расходов по дебетовым операциям пользователя превышает 100 000.
 * Используется для определения дополнительной рекомендации (например,
 * предложения по кэшбэку или инвестиционным продуктам).
 */
@Component
public class DebitSpendOverHundredThousandRuleService implements RuleService {

    private static final Logger logger = LoggerFactory.getLogger(DebitSpendOverHundredThousandRuleService.class);
    private final RecommendationsRepository repository;

    public DebitSpendOverHundredThousandRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет, превысили ли расходы по дебетовым операциям порог 100 000.
     *
     * @param userId идентификатор пользователя
     * @return true если условие выполнено, иначе false; при ошибке возвращает false и пишет в лог
     */
    @Override
    public boolean applyRule(UUID userId) {
        try {
            return repository.debitSpendOverHundredThousand(userId);
        } catch (Exception e) {
            logger.error("Ошибка при проверке правила для пользователя {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }
}