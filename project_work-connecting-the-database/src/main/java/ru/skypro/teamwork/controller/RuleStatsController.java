package ru.skypro.teamwork.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.dto.RuleStatsResponseDto;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {
    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public RuleStatsResponseDto getStats() {
        List<RuleStatsItemDto> stats = service.getAllStats();
        return new RuleStatsResponseDto(stats);
    }
}