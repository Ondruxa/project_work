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


@ExtendWith(MockitoExtension.class)
public class DebitTopUpGreaterThanSpendRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private DebitTopUpGreaterThanSpendRuleService ruleService;

    @Test
    void applyRule_ShouldReturnTrue_WhenTopUpGreaterThanSpend() {
        UUID userId = UUID.randomUUID();

        when(repository.debitTopUpGreaterThanDebitSpend(userId)).thenReturn(true);
        boolean result = ruleService.applyRule(userId);

        assertTrue(result, "Метод должен вернуть true, если пополнения больше расходов по дебетовой карте");
        verify(repository, times(1)).debitTopUpGreaterThanDebitSpend(userId);
    }

    @Test
    void applyRule_ShouldReturnFalse_WhenTopUpLessThanOrEqualToSpend() {
        UUID userId = UUID.randomUUID();

        when(repository.debitTopUpGreaterThanDebitSpend(userId)).thenReturn(false);
        boolean result = ruleService.applyRule(userId);

        assertFalse(result, "Метод должен вернуть false, если пополнения меньше или равны расходам");
        verify(repository, times(1)).debitTopUpGreaterThanDebitSpend(userId);
    }

    @Test
    void applyRule_ShouldCallRepositoryWithCorrectParameters() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(repository.debitTopUpGreaterThanDebitSpend(userId)).thenReturn(true);
        boolean result = ruleService.applyRule(userId);
        assertTrue(result);

        // Проверяем, что метод работает с правильным userId
        verify(repository).debitTopUpGreaterThanDebitSpend(userId);
        // Альтернативная проверка: что метод работает с любым UUID
        verify(repository).debitTopUpGreaterThanDebitSpend(any(UUID.class));
    }

    @Test
    void applyRule_ShouldWorkWithDifferentUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        when(repository.debitTopUpGreaterThanDebitSpend(userId1)).thenReturn(true);
        when(repository.debitTopUpGreaterThanDebitSpend(userId2)).thenReturn(false);

        boolean result1 = ruleService.applyRule(userId1);
        boolean result2 = ruleService.applyRule(userId2);

        assertAll(
                () -> assertTrue(result1, "Первый пользователь должен пополнить больше, чем потратить"),
                () -> assertFalse(result2, "Второй пользователь - пополнения должны быть меньше, либо равны тратам")
        );

        verify(repository).debitTopUpGreaterThanDebitSpend(userId1);
        verify(repository).debitTopUpGreaterThanDebitSpend(userId2);
        verify(repository, times(2)).debitTopUpGreaterThanDebitSpend(any(UUID.class));
    }

    @Test
    void constructor_ShouldInitializeRepositoryDependency() {
        RecommendationsRepository mockRepository = mock(RecommendationsRepository.class);
        DebitTopUpGreaterThanSpendRuleService service = new DebitTopUpGreaterThanSpendRuleService(mockRepository);
        assertNotNull(service);
    }

    @Test
    void applyRule_ShouldReturnFalse_WhenRepositoryThrowsException() {
        UUID userId = UUID.randomUUID();

        when(repository.debitTopUpGreaterThanDebitSpend(userId)).thenThrow(new RuntimeException("Ошибка базы данных"));
        boolean result = ruleService.applyRule(userId);

        assertFalse(result, "Возврат false при ошибке в репозитории");
        verify(repository, times(1)).debitTopUpGreaterThanDebitSpend(userId);
    }
}
