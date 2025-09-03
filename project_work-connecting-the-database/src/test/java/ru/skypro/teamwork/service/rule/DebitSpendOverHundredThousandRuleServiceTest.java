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
public class DebitSpendOverHundredThousandRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private DebitSpendOverHundredThousandRuleService ruleService;

    @Test
    void applyRule_ShouldReturnTrue_WhenUserSpentOverHundredThousand() {
        UUID userId = UUID.randomUUID();
        when(repository.debitSpendOverHundredThousand(userId)).thenReturn(true);

        //выполнение тестируемого метода
        boolean result = ruleService.applyRule(userId);

        //проверка результата
        assertTrue(result, "Метод должен вернуть true, если пользователь потратил более 100_000");

        verify(repository, times(1)).debitSpendOverHundredThousand(userId);

    }

    @Test
    void applyRule_ShouldReturnFalse_WhenUserSpentLessThanHundredThousand() {
        UUID userId = UUID.randomUUID();
        when(repository.debitSpendOverHundredThousand(userId)).thenReturn(false);

        boolean result = ruleService.applyRule(userId);
        assertFalse(result, "Метод должен вернуть false, если пользователь потратил менее 100_000");

        verify(repository, times(1)).debitSpendOverHundredThousand(userId);
    }

    @Test
    void applyRule_ShouldReturnFalse_WhenRepositoryThrowsException() {
        UUID userId = UUID.randomUUID();
        when(repository.debitSpendOverHundredThousand(userId)).thenThrow(new RuntimeException("Ошибка базы данных"));

        boolean result = ruleService.applyRule(userId);
        assertFalse(result, "Метод вернет false, если в репозитории возникло исключение");
        verify(repository, times(1)).debitSpendOverHundredThousand(userId);
    }

    @Test
    void applyRule_ShouldCallRepositoryWithCorrectUserId() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        when(repository.debitSpendOverHundredThousand(userId)).thenReturn(true);

        ruleService.applyRule(userId);

        // Проверяем, что метод был вызван с правильным userId
        verify(repository).debitSpendOverHundredThousand(any(UUID.class));
    }

    @Test
    void applyRule_ShouldHandleMultipleCallsCorrectly() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        when(repository.debitSpendOverHundredThousand(userId1)).thenReturn(true);
        when(repository.debitSpendOverHundredThousand(userId2)).thenReturn(false);

        boolean result1 = ruleService.applyRule(userId1);
        boolean result2 = ruleService.applyRule(userId2);

        assertAll(
                () -> assertTrue(result1, "Первый вызов должен вернуть true"),
                () -> assertFalse(result2, "Второй вызов должен вернуть false")
        );

        verify(repository).debitSpendOverHundredThousand(userId1);
        verify(repository).debitSpendOverHundredThousand(userId2);
        verify(repository, times(2)).debitSpendOverHundredThousand(any(UUID.class));
    }

}
