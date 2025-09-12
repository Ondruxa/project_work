package ru.skypro.teamwork.service.ruleset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.service.rule.HasDebitProductRuleService;
import ru.skypro.teamwork.service.rule.HasNoInvestProductRuleService;
import ru.skypro.teamwork.service.rule.SavingTopUpOverThousandRuleService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestRuleSetServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID EXPECTED_PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String EXPECTED_TITLE = "Invest 500";
    private static final String EXPECTED_DESCRIPTION = """
            Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!
            Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года
            и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность
            разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями.
            Откройте ИИС сегодня и станьте ближе к финансовой независимости!""";

    @Mock
    private HasDebitProductRuleService debitRule;

    @Mock
    private HasNoInvestProductRuleService noInvestRule;

    @Mock
    private SavingTopUpOverThousandRuleService savingRule;

    @InjectMocks
    private InvestRuleSetService investRuleSetService;

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnRecommendation() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(noInvestRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(savingRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация должна присутствовать");
        RecommendationDto recommendation = result.get();

        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule).applyRule(TEST_USER_ID);
        verify(savingRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenDebitRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule, never()).applyRule(any());
        verify(savingRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenNoInvestRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(noInvestRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule).applyRule(TEST_USER_ID);
        verify(savingRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WhenSavingRuleFails_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(noInvestRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(savingRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule).applyRule(TEST_USER_ID);
        verify(savingRule).applyRule(TEST_USER_ID);
    }

    @Test
    void applyRule_WhenMultipleRulesFail_ShouldReturnEmpty() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(TEST_USER_ID);

        // Assert
        assertFalse(result.isPresent(), "Рекомендация должна отсутствовать");

        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule, never()).applyRule(any());
        verify(savingRule, never()).applyRule(any());
    }

    @Test
    void applyRule_WithDifferentUserId_ShouldCallRulesWithCorrectUserId() {
        // Arrange
        UUID differentUserId = UUID.fromString("987e6543-e21b-45d3-b789-426614174999");
        when(debitRule.applyRule(differentUserId)).thenReturn(true);
        when(noInvestRule.applyRule(differentUserId)).thenReturn(true);
        when(savingRule.applyRule(differentUserId)).thenReturn(true);

        // Act
        Optional<RecommendationDto> result = investRuleSetService.applyRule(differentUserId);

        // Assert
        assertTrue(result.isPresent(), "Рекомендация должна присутствовать");

        verify(debitRule).applyRule(differentUserId);
        verify(noInvestRule).applyRule(differentUserId);
        verify(savingRule).applyRule(differentUserId);
    }

    @Test
    void applyRule_WhenAllRulesPass_ShouldReturnCorrectRecommendationData() {
        // Arrange
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(noInvestRule.applyRule(TEST_USER_ID)).thenReturn(true);
        when(savingRule.applyRule(TEST_USER_ID)).thenReturn(true);

        // Act
        RecommendationDto recommendation = investRuleSetService.applyRule(TEST_USER_ID).get();

        // Assert
        assertEquals(EXPECTED_PRODUCT_ID, recommendation.getProductId());
        assertEquals(EXPECTED_TITLE, recommendation.getTitle());
        assertEquals(EXPECTED_DESCRIPTION, recommendation.getDescription());
        assertNotNull(recommendation.toString());
    }

    @Test
    void applyRule_ShouldUseShortCircuitEvaluation() {
        // Arrange - только первое правило вернет false
        when(debitRule.applyRule(TEST_USER_ID)).thenReturn(false);

        // Act
        investRuleSetService.applyRule(TEST_USER_ID);

        // Assert - проверяем, что только первое правило было вызвано
        verify(debitRule).applyRule(TEST_USER_ID);
        verify(noInvestRule, never()).applyRule(any());
        verify(savingRule, never()).applyRule(any());
    }
}
