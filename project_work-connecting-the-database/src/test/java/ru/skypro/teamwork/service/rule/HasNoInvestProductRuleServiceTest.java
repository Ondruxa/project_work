package ru.skypro.teamwork.service.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class HasNoInvestProductRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private HasNoInvestProductRuleService ruleService;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    void applyRule_shouldReturnTrue_whenUserHasNoInvestProducts() {
        when(repository.userHasNoInvestProduct(testUserId)).thenReturn(true);
        boolean result = ruleService.applyRule(testUserId);
        assertTrue(result);

        verify(repository, times(1)).userHasNoInvestProduct(testUserId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_shouldReturnFalse_whenUserHasInvestProducts() {
        when(repository.userHasNoInvestProduct(testUserId)).thenReturn(false);
        boolean result = ruleService.applyRule(testUserId);
        assertFalse(result);

        verify(repository, times(1)).userHasNoInvestProduct(testUserId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_shouldThrowException_whenRepositoryThrowsException() {
        when(repository.userHasNoInvestProduct(any(UUID.class)))
                .thenThrow(new RuntimeException("Ошибка базы данных."));

        assertThrows(RuntimeException.class, () -> ruleService.applyRule(testUserId));
        verify(repository, times(1)).userHasNoInvestProduct(testUserId);
    }

    @Test
    void applyRule_shouldThrowNPE_whenUserIdIsNull() {
       when(repository.userHasNoInvestProduct(null)).thenReturn(false);
       assertDoesNotThrow(() -> ruleService.applyRule(null));
       verify(repository, times(1)).userHasNoInvestProduct(null);
    }
}
