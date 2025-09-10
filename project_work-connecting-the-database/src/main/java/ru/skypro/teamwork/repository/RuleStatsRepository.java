package ru.skypro.teamwork.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.repository.sql.RuleStatsSql;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий статистики срабатываний правил.
 * <p>
 * Использует JDBC для инкремента счётчиков и получения агрегированной статистики.
 */
@Repository
public class RuleStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate основной JdbcTemplate
     */
    public RuleStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Инкрементирует счётчик срабатываний для указанного правила (upsert).
     * @param ruleId id правила
     */
    public void increment(UUID ruleId) {
        jdbcTemplate.update(RuleStatsSql.UPSERT_INCREMENT, ruleId);
    }

    /**
     * Возвращает список статистики с учётом отсутствующих записей (нули).
     * @return список элементов статистики
     */
    public List<RuleStatsItemDto> findAllWithZero() {
        return jdbcTemplate.query(
                RuleStatsSql.FIND_ALL_WITH_ZERO,
                (rs, i) -> new RuleStatsItemDto(
                        rs.getObject("rule_id", java.util.UUID.class),
                        rs.getLong("cnt")
                )
        );
    }

    /**
     * Удаляет статистику по ruleId.
     * @param ruleId id правила
     * @return число удалённых строк
     */
    public int delete(UUID ruleId) {
        return jdbcTemplate.update("DELETE FROM rule_stats WHERE rule_id = ?", ruleId);
    }
}
