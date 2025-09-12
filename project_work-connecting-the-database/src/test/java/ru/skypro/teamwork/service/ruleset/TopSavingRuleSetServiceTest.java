package ru.skypro.teamwork.service.ruleset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.DebitTopUpGreaterThanSpendRuleService;
import ru.skypro.teamwork.service.rule.HasDebitProductRuleService;
import ru.skypro.teamwork.service.rule.TopUpOverFiftyThousandRuleService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopSavingRuleSetServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID EXPECTED_PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String EXPECTED_TITLE = "Top Saving";
    private static final String EXPECTED_DESCRIPTION = """
            Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!
            Преимущества «Копилки»:
            Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
            Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
            Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.
            Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!""";

    @Mock
    private HasDebitProductRuleService debitRule;

    @Mock
    private TopUpOverFiftyThousandRuleService topUpOverFiftyThousandRule;

    @Mock
    private DebitTopUpGreaterThanSpendRuleService debitTopUpGreaterThanSpendRule;

    @InjectMocks
    private TopSavingRuleSetService topSavingRuleSetService;

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnTopSavingRecommendation() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация Top Saving должна присутствовать");
        RecommendationDto recommendation = result.get();

        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenDebitRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при отсутствии дебетового продукта");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule, never()).applyRule(any());
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenTopUpOverFiftyThousandRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при недостаточных пополнениях");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenDebitTopUpGreaterThanSpendRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать при недостаточном превышении пополнений над расходами");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenFirstRuleFails_ShouldUseShortCircuitEvaluation() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule, never()).applyRule(any());
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenSecondRuleFails_ShouldNotCallThirdRule() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WithDifferentUserId_ShouldCallRulesWithCorrectUserId() {
        // Arrange
        UUID differentUserId = UUID.fromString("987e6543-e21b-45d3-b789-426614174999");
        when(debitRule.applyRule(differentUserId)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(differentUserId)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(differentUserId)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(differentUserId);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация должна присутствовать для другого пользователя");

        verify(debitRule).applyRule(differentUserId);
        verify(topUpOverFiftyThousandRule).applyRule(differentUserId);
        verify(debitTopUpGreaterThanSpendRule).applyRule(differentUserId);
    }

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnCorrectRecommendationData() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        RecommendationDto recommendation = topSavingRuleSetService.applyRule(TEST_USER_ID).get();

        // Assert
        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());
        assertNotNull(recommendation.toString());
    }

    @Test
    void applyRule_ShouldReturnEmptyOptionalWhenAnyRuleFails() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertTrue(result.isEmpty(), "Должен возвращаться пустой Optional при неудаче любого правила");
    }

    @Test
    void applyRule_ShouldCallAllRulesInCorrectOrder() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(topUpOverFiftyThousandRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(debitTopUpGreaterThanSpendRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        topSavingRuleSetService.applyRule(TEST_USER_ID);

        // Assert - проверяем порядок вызовов через порядок объявления в классе
        verify(debitRule).applyRule(TEST_USER_ID);
        verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);

        // Можно также проверить порядок с помощью inOrder
        var inOrder = inOrder(debitRule, topUpOverFiftyThousandRule, debitTopUpGreaterThanSpendRule);
        inOrder.verify(debitRule).applyRule(TEST_USER_ID);
        inOrder.verify(topUpOverFiftyThousandRule).applyRule(TEST_USER_ID);
        inOrder.verify(debitTopUpGreaterThanSpendRule).applyRule(TEST_USER_ID);
    }
}
