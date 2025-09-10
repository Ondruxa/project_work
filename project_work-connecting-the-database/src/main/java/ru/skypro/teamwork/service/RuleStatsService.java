package ru.skypro.teamwork.service;

import ru.skypro.teamwork.dto.RuleStatsItemDto;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы со статистикой срабатывания динамических правил.
 * Предоставляет операции инкремента счётчика, удаления статистики
 * и получения полного списка статистики с нулевыми значениями.
 */
public interface RuleStatsService {
    void incrementRuleCount(UUID ruleId);
    void deleteRuleStats(UUID ruleId);
    List<RuleStatsItemDto> getAllStats();
}