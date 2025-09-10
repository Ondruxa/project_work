package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRuleService;
import ru.skypro.teamwork.service.rule.HasDebitProductRuleService;
import ru.skypro.teamwork.service.rule.TopUpOverFiftyThousandRuleService;
import ru.skypro.teamwork.service.rule.RuleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Набор правил для рекомендации накопительного (сберегательного) продукта «Top Saving».
 * Условия включают:
 * 1) Пользователь имеет дебетовый продукт
 * 2) У пользователя есть существенные пополнения (операции > 50 000)
 * 3) Пополнения по дебету в целом превышают расходы
 */
@Component
public class TopSavingRuleSetService implements RecommendationRuleSetService {

    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String TITLE = "Top Saving";
    private static final String DESCRIPTION = """
            Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!
            Преимущества «Копилки»:
            Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
            Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
            Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.
            Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!""";

    private final List<RuleService> rules;

    public TopSavingRuleSetService(
            HasDebitProductRuleService debitRule,
            TopUpOverFiftyThousandRuleService topUpOverFiftyThousandRule,
            DebitTopUpGreaterThanSpendRuleService debitTopUpGreaterThanSpendRule
    ) {
        this.rules = List.of(debitRule, topUpOverFiftyThousandRule, debitTopUpGreaterThanSpendRule);
    }

    /**
     * Применяет набор правил и формирует рекомендацию продукта «Top Saving».
     * @param userId идентификатор пользователя
     * @return рекомендация при успешном прохождении правил или пусто
     */
    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(r -> r.applyRule(userId));
        if (!passed) {
            return Optional.empty();
        }
        return Optional.of(new RecommendationDto(PRODUCT_ID, TITLE, DESCRIPTION));
    }
}