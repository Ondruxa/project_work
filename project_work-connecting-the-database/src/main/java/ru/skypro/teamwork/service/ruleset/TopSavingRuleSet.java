package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRule;
import ru.skypro.teamwork.service.rule.HasDebitProductRule;
import ru.skypro.teamwork.service.rule.TopUpOverFiftyThousandRule;
import ru.skypro.teamwork.service.rule.Rule;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {
    private final List<Rule> rules;
    private final UUID productId = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    public TopSavingRuleSet(
            HasDebitProductRule debitRule,
            TopUpOverFiftyThousandRule topUpOverFiftyThousandRule,
            DebitTopUpGreaterThanSpendRule debitTopUpGreaterThanSpendRule
    ) {
        this.rules = List.of(debitRule, topUpOverFiftyThousandRule, debitTopUpGreaterThanSpendRule);
    }

    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(rule -> rule.applyRule(userId));
        if (passed) {
            return Optional.of(new RecommendationDto(
                    productId,
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                            "\n" +
                            "Преимущества «Копилки»:\n" +
                            "\n" +
                            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                            "\n" +
                            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                            "\n" +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                            "\n" +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
            ));
        }
        return Optional.empty();
    }
}