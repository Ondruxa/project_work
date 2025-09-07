package ru.skypro.teamwork.service;

import ru.skypro.teamwork.dto.RuleStatsItemDto;
import java.util.List;
import java.util.UUID;

public interface RuleStatsService {
    void incrementRuleCount(UUID ruleId);
    void deleteRuleStats(UUID ruleId);
    List<RuleStatsItemDto> getAllStats();
}