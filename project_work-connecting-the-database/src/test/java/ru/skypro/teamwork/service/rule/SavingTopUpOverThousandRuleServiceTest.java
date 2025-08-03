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
public class SavingTopUpOverThousandRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private SavingTopUpOverThousandRuleService ruleService;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    void applyRule_shouldReturnTrue_whenUserHasSavingTopUpOverThousand() {
        when(repository.userSavingTopUpOverThousand(testUserId)).thenReturn(true);
        boolean result = ruleService.applyRule(testUserId);

        assertTrue(result);
        verify(repository, times(1)).userSavingTopUpOverThousand(testUserId);
    }

    @Test
    void applyRule_shouldReturnFalse_whenUserHasNoSavingTopUpOverThousand() {
        when(repository.userSavingTopUpOverThousand(testUserId)).thenReturn(false);
        boolean result = ruleService.applyRule(testUserId);

        assertFalse(result);
        verify(repository, times(1)).userSavingTopUpOverThousand(testUserId);
    }

    @Test
    void applyRule_shouldThrowException_whenRepositoryThrowsException() {
        when(repository.userSavingTopUpOverThousand(testUserId))
                .thenThrow(new RuntimeException("Ошибка базы данных."));

        assertThrows(RuntimeException.class, () -> ruleService.applyRule(testUserId));
        verify(repository, times(1)).userSavingTopUpOverThousand(testUserId);

    }

    @Test
    void applyRule_shouldThrowNPE_whenUserIdIsNull() {
        when(repository.userSavingTopUpOverThousand(null)).thenReturn(false);
        assertDoesNotThrow(() -> ruleService.applyRule(null));
        verify(repository, times(1)).userSavingTopUpOverThousand(null);
    }

    @Test
    void applyRule_shouldUseAnyUUID_whenCalled() {
        when(repository.userSavingTopUpOverThousand(any(UUID.class))).thenReturn(true);
        boolean result = ruleService.applyRule(testUserId);

        assertTrue(result);
        verify(repository, times(1)).userSavingTopUpOverThousand(any(UUID.class));
    }
}
