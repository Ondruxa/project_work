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
public class TopUpOverFiftyThousandRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private TopUpOverFiftyThousandRuleService ruleService;

    private final UUID userId = UUID.fromString("123e456-e12b-12d3-b654-4255252417000");

    @Test
    void applyRule_WhenUserHasDebitTopUpOverFiftyThousand_ShouldReturnTrue() {
        when(repository.userHasTopUpOverFiftyThousand(userId)).thenReturn(true);

        boolean result = ruleService.applyRule(userId);
        assertTrue(result);
        verify(repository, times(1)).userHasTopUpOverFiftyThousand(userId);
    }

    @Test
    void applyRule_WhenUserHasSavingTopUpOverFiftyThousand_ShouldReturnTrue() {
        when(repository.userHasTopUpOverFiftyThousand(userId)).thenReturn(true);

        boolean result = ruleService.applyRule(userId);
        assertTrue(result);
        verify(repository, times(1)).userHasTopUpOverFiftyThousand(userId);
    }

    @Test
    void applyRule_WhenUserHasNoTopUpOverFiftyThousand_ShouldReturnFalse() {
        when(repository.userHasTopUpOverFiftyThousand(userId)).thenReturn(false);

        boolean result = ruleService.applyRule(userId);
        assertFalse(result);
        verify(repository, times(1)).userHasTopUpOverFiftyThousand(userId);
    }

    @Test
    void applyRule_WithDifferentUserIds_ShouldCallRepositoryWithCorrectParameter() {
        UUID differentUserId = UUID.fromString("987e6543-e21b-45d3-b654-789654123000");
        when(repository.userHasTopUpOverFiftyThousand(differentUserId)).thenReturn(true);

        boolean result = ruleService.applyRule(differentUserId);
        assertTrue(result);
        verify(repository, times(1)).userHasTopUpOverFiftyThousand(differentUserId);
        verify(repository, never()).userHasTopUpOverFiftyThousand(userId);
    }

    @Test
    void applyRule_ShouldCallRepositoryOnlyOnce() {
        when(repository.userHasTopUpOverFiftyThousand(userId)).thenReturn(true);
        ruleService.applyRule(userId);

        verify(repository, times(1)).userHasTopUpOverFiftyThousand(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_WhenRepositoryThrowsException_ShouldPropagateException() {
        when(repository.userHasTopUpOverFiftyThousand(userId))
                .thenThrow(new RuntimeException("Ошибка в базе данных"));

        assertThrows(RuntimeException.class, () -> ruleService.applyRule(userId));
        verify(repository, times(1)).userHasTopUpOverFiftyThousand(userId);
    }
}
