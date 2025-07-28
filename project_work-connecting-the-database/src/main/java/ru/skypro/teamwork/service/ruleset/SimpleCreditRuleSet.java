package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitSpendOverHundredThousandRule;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRule;
import ru.skypro.teamwork.service.rule.HasNoCreditProductRule;
import ru.skypro.teamwork.service.rule.Rule;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {
    private final List<Rule> rules;
    private final UUID productId = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");

    public SimpleCreditRuleSet(
            HasNoCreditProductRule noCreditRule,
            DebitTopUpGreaterThanSpendRule debitTopUpGreaterThanSpendRule,
            DebitSpendOverHundredThousandRule debitSpendOverHundredThousandRule
    ) {
        this.rules = List.of(noCreditRule, debitTopUpGreaterThanSpendRule, debitSpendOverHundredThousandRule);
    }

    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(rule -> rule.applyRule(userId));
        if (passed) {
            return Optional.of(new RecommendationDto(
                    productId,
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами!\n" +
                            "\n" +
                            "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                            "\n" +
                            "Почему выбирают нас:\n" +
                            "\n" +
                            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                            "\n" +
                            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                            "\n" +
                            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                            "\n" +
                            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"
            ));
        }
        return Optional.empty();
    }
}