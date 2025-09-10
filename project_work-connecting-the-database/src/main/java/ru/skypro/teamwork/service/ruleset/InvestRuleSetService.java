package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.HasDebitProductRuleService;
import ru.skypro.teamwork.service.rule.HasNoInvestProductRuleService;
import ru.skypro.teamwork.service.rule.SavingTopUpOverThousandRuleService;
import ru.skypro.teamwork.service.rule.RuleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Набор правил для рекомендации инвестиционного продукта (ИИС / Invest 500).
 * Правила включают:
 * 1) Пользователь имеет дебетовый продукт
 * 2) Пользователь ещё не имеет инвестиционных продуктов
 * 3) Пользователь активно пополняет накопительные продукты (порог > 1000)
 */
@Component
public class InvestRuleSetService implements RecommendationRuleSetService {

    private static final UUID INVEST_PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String TITLE = "Invest 500";
    private static final String DESCRIPTION = """
            Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!
            Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года
            и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность
            разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями.
            Откройте ИИС сегодня и станьте ближе к финансовой независимости!""";

    private final List<RuleService> rules;

    public InvestRuleSetService(
            HasDebitProductRuleService debitRule,
            HasNoInvestProductRuleService noInvestRule,
            SavingTopUpOverThousandRuleService savingRule
    ) {
        this.rules = List.of(debitRule, noInvestRule, savingRule);
    }

    /**
     * Применяет все правила набора для конкретного пользователя.
     * @param userId идентификатор пользователя
     * @return рекомендация инвест‑продукта при успешном прохождении всех условий
     */
    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(rule -> rule.applyRule(userId));
        if (!passed) {
            return Optional.empty();
        }
        return Optional.of(new RecommendationDto(INVEST_PRODUCT_ID, TITLE, DESCRIPTION));
    }
}