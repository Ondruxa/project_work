package ru.skypro.teamwork.service.rule;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.UUID;

/**
 * Правило: пользователь совершал пополнения (суммарно или одной операцией) свыше 50 000
 * по дебетовым/сберегательным продуктам (в зависимости от реализации в репозитории).
 * Служит индикатором значительной активности и может влиять на рекомендации
 * (повышенные лимиты, инвестиционные продукты и т.п.).
 */
@Component
public class TopUpOverFiftyThousandRuleService implements RuleService {

    private final RecommendationsRepository repository;

    public TopUpOverFiftyThousandRuleService(RecommendationsRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет, превышает ли сумма (или разовая операция) пополнений порог 50 000.
     *
     * @param userId идентификатор пользователя
     * @return true если условие выполнено, иначе false
     */
    @Override
    public boolean applyRule(UUID userId) {
        return repository.userHasTopUpOverFiftyThousand(userId);
    }
}