package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.dto.RuleStatsResponseDto;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleStatsControllerTest {

    @Mock
    private RuleStatsService ruleStatsService;

    @InjectMocks
    private RuleStatsController ruleStatsController;

    @Test
    void getStats_ShouldReturnRuleStatsResponseDto_WhenServiceReturnsData() {

        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();
        List<RuleStatsItemDto> expectedStats = List.of(
                new RuleStatsItemDto(ruleId1, 10L),
                new RuleStatsItemDto(ruleId2, 5L)
        );

        when(ruleStatsService.getAllStats()).thenReturn(expectedStats);

        RuleStatsResponseDto result = ruleStatsController.getStats();

        assertNotNull(result, "Ответ не должен быть нулевым");
        assertEquals(expectedStats, result.getStats(), "Статистика должна соответствовать ожидаемым данным");
        assertEquals(2, result.getStats().size(), "Должен содержать 2 элемента статистики");

        verify(ruleStatsService, times(1)).getAllStats();
        verifyNoMoreInteractions(ruleStatsService);

    }

    @Test
    void getStats_ShouldReturnEmptyResponse_WhenServiceReturnsEmptyList() {

        List<RuleStatsItemDto> emptyStats = List.of();
        when(ruleStatsService.getAllStats()).thenReturn(emptyStats);

        RuleStatsResponseDto result = ruleStatsController.getStats();

        assertNotNull(result);
        assertTrue(result.getStats().isEmpty());

        verify(ruleStatsService, times(1)).getAllStats();
    }

    @Test
    void getStats_ShouldReturnResponseWithSingleItem_WhenServiceReturnsSingleItem() {

        UUID ruleId = UUID.randomUUID();
        List<RuleStatsItemDto> singleStat = List.of(
                new RuleStatsItemDto(ruleId, 1L)
        );

        when(ruleStatsService.getAllStats()).thenReturn(singleStat);

        RuleStatsResponseDto result = ruleStatsController.getStats();

        assertNotNull(result);
        assertEquals(1, result.getStats().size());
        assertEquals(ruleId, result.getStats().get(0).getRule_id());
        assertEquals(1L, result.getStats().get(0).getCount());

        verify(ruleStatsService, times(1)).getAllStats();
    }

    @Test
    void getStats_ShouldCallServiceExactlyOnce_WhenInvoked() {

        when(ruleStatsService.getAllStats()).thenReturn(List.of());

        ruleStatsController.getStats();

        verify(ruleStatsService, times(1)).getAllStats();
        verifyNoMoreInteractions(ruleStatsService);
    }

    @Test
    void getStats_ShouldReturnNewInstance_EachTime() {

        UUID ruleId = UUID.randomUUID();
        List<RuleStatsItemDto> stats = List.of(
                new RuleStatsItemDto(ruleId, 3L)
        );

        when(ruleStatsService.getAllStats()).thenReturn(stats);

        RuleStatsResponseDto result1 = ruleStatsController.getStats();
        RuleStatsResponseDto result2 = ruleStatsController.getStats();

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getStats(), result2.getStats());

        verify(ruleStatsService, times(2)).getAllStats();
    }

    @Test
    void getStats_ShouldReturnCorrectDataStructure_WhenMultipleItems() {

        UUID ruleId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID ruleId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID ruleId3 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

        List<RuleStatsItemDto> stats = List.of(
                new RuleStatsItemDto(ruleId1, 100L),
                new RuleStatsItemDto(ruleId2, 50L),
                new RuleStatsItemDto(ruleId3, 25L)
        );

        when(ruleStatsService.getAllStats()).thenReturn(stats);

        RuleStatsResponseDto result = ruleStatsController.getStats();

        assertNotNull(result);
        assertEquals(3, result.getStats().size());

        assertEquals(ruleId1, result.getStats().get(0).getRule_id());
        assertEquals(100L, result.getStats().get(0).getCount());

        assertEquals(ruleId2, result.getStats().get(1).getRule_id());
        assertEquals(50L, result.getStats().get(1).getCount());

        assertEquals(ruleId3, result.getStats().get(2).getRule_id());
        assertEquals(25L, result.getStats().get(2).getCount());

        verify(ruleStatsService, times(1)).getAllStats();
    }
}

