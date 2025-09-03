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
public class HasNoCreditProductRuleServiceTest {

    @Mock
    private RecommendationsRepository repository;

    @InjectMocks
    private HasNoCreditProductRuleService ruleService;

    private final UUID userId = UUID.fromString("123e456-e12b-12d3-b654-4255252417000");

    @Test
    void applyRule_WhenUserHasNoCreditProducts_ShouldReturnTrue() {
        when(repository.userHasNoCreditProduct(userId)).thenReturn(true);
        boolean result = ruleService.applyRule(userId);

        assertTrue(result);
        verify(repository, times(1)).userHasNoCreditProduct(userId);
    }

    @Test
    void applyRule_WhenUserHasCreditProducts_ShouldReturnFals() {
        when(repository.userHasNoCreditProduct(userId)).thenReturn(false);
        boolean result = ruleService.applyRule(userId);

        assertFalse(result);
        verify(repository, times(1)).userHasNoCreditProduct(userId);
    }

    @Test
    void applyRule_WithDifferentUserIds_ShouldCallRepositoryWithCorrectParameter() {
        UUID differentUserId = UUID.fromString("123e456-e12b-12d3-b654-4255252417000");
        when(repository.userHasNoCreditProduct(differentUserId)).thenReturn(true);
        boolean result = ruleService.applyRule(differentUserId);

        assertTrue(result);
        verify(repository, times(1)).userHasNoCreditProduct(differentUserId);
        verify(repository, times(1)).userHasNoCreditProduct(userId);
    }

    @Test
    void applyRule_ShouldCallRepositoryOnlyOnce() {
        when(repository.userHasNoCreditProduct(userId)).thenReturn(true);
        ruleService.applyRule(userId);

        verify(repository, times(1)).userHasNoCreditProduct(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void applyRule_WhenRepositoryThrowsException_ShouldPropagateException() {
        when(repository.userHasNoCreditProduct(userId))
                .thenThrow(new RuntimeException("Ошибка в базе данных"));
        assertThrows(RuntimeException.class, () -> ruleService.applyRule(userId));
    }


}
