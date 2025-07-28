package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.HasDebitProductRule;
import ru.skypro.teamwork.service.rule.HasNoInvestProductRule;
import ru.skypro.teamwork.service.rule.SavingTopUpOverThousandRule;
import ru.skypro.teamwork.service.rule.Rule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InvestRuleSet implements RecommendationRuleSet {
    private final List<Rule> rules;
    private final UUID productId = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");

    public InvestRuleSet(
            HasDebitProductRule debitRule,
            HasNoInvestProductRule noInvestRule,
            SavingTopUpOverThousandRule savingRule
    ) {
        this.rules = List.of(debitRule, noInvestRule, savingRule);
    }

    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(rule -> rule.applyRule(userId));
        if (passed) {
            return Optional.of(new RecommendationDto(productId, "Invest 500",
                    "Откройте свой путь к успеху " +
                    "с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
                    "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
                    "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                    "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. " +
                            "Откройте ИИС сегодня и станьте ближе к финансовой независимости!"));
        }
        return Optional.empty();
    }
}