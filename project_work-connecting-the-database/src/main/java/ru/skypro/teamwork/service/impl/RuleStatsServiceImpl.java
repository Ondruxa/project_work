package ru.skypro.teamwork.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.repository.RuleStatsRepository;
import ru.skypro.teamwork.service.RuleStatsService;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса статистики применения динамических правил.
 * Позволяет инкрементировать счётчик срабатываний, удалять статистику
 * и получать агрегированную информацию по всем правилам.
 */
@Service
public class RuleStatsServiceImpl implements RuleStatsService {
    private final RuleStatsRepository repository;

    public RuleStatsServiceImpl(RuleStatsRepository repository) {
        this.repository = repository;
    }

    /**
     * Увеличивает счётчик срабатываний правила.
     *
     * @param ruleId идентификатор правила (UUID)
     */
    @Transactional
    @Override
    public void incrementRuleCount(UUID ruleId) {
        repository.increment(ruleId);
    }

    /**
     * Удаляет статистику по конкретному правилу.
     *
     * @param ruleId идентификатор правила (UUID)
     */
    @Transactional
    @Override
    public void deleteRuleStats(UUID ruleId) {
        repository.delete(ruleId);
    }

    /**
     * Возвращает статистику по всем динамическим правилам
     * (включая те, у которых счётчик равен нулю).
     *
     * @return список DTO со статистикой
     */
    @Override
    public List<RuleStatsItemDto> getAllStats() {
        // Возвращаем только статистику для динамических правил
        return repository.findAllWithZero();
    }
}
