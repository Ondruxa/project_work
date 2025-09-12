package ru.skypro.teamwork.service.ruleset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitSpendOverHundredThousandRuleService;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRuleService;
import ru.skypro.teamwork.service.rule.HasNoCreditProductRuleService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimpleCreditRuleSetServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID EXPECTED_PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String EXPECTED_TITLE = "Простой кредит";

    // Используем точное описание из SimpleCreditRuleSetService
    private static final String EXPECTED_DESCRIPTION = """
            Откройте мир выгодных кредитов с нами!
            Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
            Почему выбирают нас:
            Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
            Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
            Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
            Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!""";

    @Mock
    private HasNoCreditProductRuleService noCreditRule;

    @Mock
    private DebitTopUpGreaterThanSpendRuleService debitTopUpGreaterThanSpendRule;

    @Mock
    private DebitSpendOverHundredThousandRuleService debitSpendOverHundredThousandRule;

    @InjectMocks
    private SimpleCreditRuleSetService simpleCreditRuleSetService;

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnCreditRecommendation() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitSpendOverHundredThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация кредита должна присутствовать");
        RecommendationDto recommendation = result.get();

        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());

        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
        verify(debitSpendOverHundredThousandRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenNoCreditRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при наличии кредитных продуктов");

        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
        verify(debitSpendOverHundredThousandRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenDebitTopUpRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при недостаточных пополнениях");

        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
        verify(debitSpendOverHundredThousandRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenDebitSpendRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitSpendOverHundredThousandRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при недостаточных расходах");

        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
        verify(debitSpendOverHundredThousandRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenFirstRuleFails_ShouldUseShortCircuitEvaluation() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
        verify(debitSpendOverHundredThousandRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenSecondRuleFails_ShouldNotCallThirdRule() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        verify(noCreditRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
        verify(debitSpendOverHundredThousandRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WithDifferentUserId_ShouldCallRulesWithCorrectUserId() {
        // Arrange
        UUID differentUserId = UUID.fromString("987e6543-e21b-45d3-b789-426614174999");
        when(noCreditRule.applyRule(differentUserId)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(differentUserId)).thenReturn(true);
        when(debitSpendOverHundredThousandRule.applyRule(differentUserId)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(differentUserId);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация должна присутствовать для другого пользователя");

        verify(noCreditRule).applyRule(differentUserId);
        verify(debitTopUpGreaterThanSpendRule).applyRule(differentUserId);
        verify(debitSpendOverHundredThousandRule).applyRule(differentUserId);
    }

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnCorrectRecommendationData() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitSpendOverHundredThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        RecommendationDto recommendation = simpleCreditRuleSetService.applyRule(TEST_USER_ID).get();

        // Assert
        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());
        assertNotNull(recommendation.toString());
    }

    @Test
    void applyRule_WhenMultipleRulesFail_ShouldReturnEmpty() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при нескольких неудачных правилах");
    }

    @Test
    void applyRule_ShouldReturnEmptyOptionalWhenAnyRuleFails() {
        // Arrange
        when(noCreditRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitSpendOverHundredThousandRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = simpleCreditRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertTrue(result.isEmpty(), "Должен возвращаться пустой Optional при неудаче любого правила");
    }
}
