package ru.skypro.teamwork.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.dto.RuleStatsResponseDto;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;

/**
 * Контроллер предоставления статистики срабатываний правил при запросе рекомендаций.
 * <p>
 * Базовый путь: /rule
 * <ul>
 *   <li>GET /rule/stats — агрегированная статистика по всем правилам.</li>
 * </ul>
 */
@RestController
@RequestMapping("/rule")
public class RuleStatsController {
    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    /**
     * Возвращает статистику срабатываний для всех динамических правил.
     *
     * @return DTO с коллекцией статистических записей
     */
    @GetMapping("/stats")
    public RuleStatsResponseDto getStats() {
        List<RuleStatsItemDto> stats = service.getAllStats();
        return new RuleStatsResponseDto(stats);
    }
}