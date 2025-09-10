package ru.skypro.teamwork.service.ruleset;

import org.springframework.stereotype.Component;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitSpendOverHundredThousandRuleService;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRuleService;
import ru.skypro.teamwork.service.rule.HasNoCreditProductRuleService;
import ru.skypro.teamwork.service.rule.RuleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Набор правил для предложения простого кредитного продукта.
 * Условия включают:
 * 1) У пользователя нет кредитных продуктов
 * 2) Пополнения по дебету превышают расходы
 * 3) Расходы по дебету превышают фиксированный порог (100 000)
 */
@Component
public class SimpleCreditRuleSetService implements RecommendationRuleSetService {

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String TITLE = "Простой кредит";
    private static final String DESCRIPTION = """
            Откройте мир выгодных кредитов с нами!
            Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
            Почему выбирают нас:
            Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
            Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
            Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
            Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!""";

    private final List<RuleService> rules;

    public SimpleCreditRuleSetService(
            HasNoCreditProductRuleService noCreditRule,
            DebitTopUpGreaterThanSpendRuleService debitTopUpGreaterThanSpendRule,
            DebitSpendOverHundredThousandRuleService debitSpendOverHundredThousandRule
    ) {
        this.rules = List.of(noCreditRule, debitTopUpGreaterThanSpendRule, debitSpendOverHundredThousandRule);
    }

    /**
     * Применяет набор кредитных правил. Все условия должны быть выполнены.
     * @param userId идентификатор пользователя
     * @return рекомендация кредитного продукта при успехе
     */
    @Override
    public Optional<RecommendationDto> applyRule(UUID userId) {
        boolean passed = rules.stream().allMatch(rule -> rule.applyRule(userId));
        if (!passed) {
            return Optional.empty();
        }
        return Optional.of(new RecommendationDto(PRODUCT_ID, TITLE, DESCRIPTION));
    }
}