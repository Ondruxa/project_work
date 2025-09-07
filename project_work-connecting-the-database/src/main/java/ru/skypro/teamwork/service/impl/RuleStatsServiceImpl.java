package ru.skypro.teamwork.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.repository.RuleStatsRepository;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;
import java.util.UUID;

@Service
public class RuleStatsServiceImpl implements RuleStatsService {
    private final RuleStatsRepository repository;

    public RuleStatsServiceImpl(RuleStatsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void incrementRuleCount(UUID ruleId) {
        repository.increment(ruleId);
    }

    @Transactional
    @Override
    public void deleteRuleStats(UUID ruleId) {
        repository.delete(ruleId);
    }

    @Override
    public List<RuleStatsItemDto> getAllStats() {
        // Возвращаем только статистику для динамических правил
        return repository.findAllWithZero();
    }
}
