package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RuleStatsControllerIntegrationTest {
    private MockMvc mockMvc;

    @Mock
    private RuleStatsService ruleStatsService;

    @InjectMocks
    private RuleStatsController ruleStatsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ruleStatsController).build();
    }

    @Test
    void getStats_ShouldReturn200Ok_WithCorrectJsonStructure() throws Exception {

        UUID ruleId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        List<RuleStatsItemDto> stats = List.of(new RuleStatsItemDto(ruleId, 42L));

        when(ruleStatsService.getAllStats()).thenReturn(stats);

        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats").isArray())
                .andExpect(jsonPath("$.stats.length()").value(1))
                .andExpect(jsonPath("$.stats[0].rule_id").value(ruleId.toString()))
                .andExpect(jsonPath("$.stats[0].count").value(42));
    }

    @Test
    void getStats_ShouldReturnEmptyArray_WhenNoStatsAvailable() throws Exception {

        when(ruleStatsService.getAllStats()).thenReturn(List.of());

        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats").isEmpty());
    }
}

