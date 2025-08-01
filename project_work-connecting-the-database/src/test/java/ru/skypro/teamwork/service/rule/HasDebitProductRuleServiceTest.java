package ru.skypro.teamwork.service.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
public class HasDebitProductRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private HasDebitProductRuleService ruleService;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    void applyRule_shouldReturnTrue_whenUserHasDebitProduct() {
        when(repository.userHasDebitProduct(testUserId)).thenReturn(true);

        boolean result = ruleService.applyRule(testUserId);
        assertTrue(result);
        verify(repository, times(1)).userHasDebitProduct(testUserId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_shouldReturnFalse_whenUserHasNoDebitProduct() {
        when(repository.userHasDebitProduct(testUserId)).thenReturn(false);

        boolean result = ruleService.applyRule(testUserId);
        assertFalse(result);
        verify(repository, times(1)).userHasDebitProduct(testUserId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_shouldHandleRepositoryException_gracefully() {
        when(repository.userHasDebitProduct(testUserId))
                .thenThrow(new RuntimeException("Ошибка базы данных."));

        assertThrows(RuntimeException.class, () -> ruleService.applyRule(testUserId));

        verify(repository, times(1)).userHasDebitProduct(testUserId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @Timeout(1) //1 секунда
    void applyRule_shouldCompleteInTime() {
        when(repository.userHasDebitProduct(testUserId)).thenReturn(true);
        ruleService.applyRule(testUserId);
    }
}
