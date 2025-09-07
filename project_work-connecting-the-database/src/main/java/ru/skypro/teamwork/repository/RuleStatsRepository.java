package ru.skypro.teamwork.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skypro.teamwork.dto.RuleStatsItemDto;
import ru.skypro.teamwork.repository.sql.RuleStatsSql;

import java.util.List;
import java.util.UUID;

@Repository
public class RuleStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RuleStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void increment(UUID ruleId) {
        jdbcTemplate.update(RuleStatsSql.UPSERT_INCREMENT, ruleId);
    }

    public List<RuleStatsItemDto> findAllWithZero() {
        return jdbcTemplate.query(
                RuleStatsSql.FIND_ALL_WITH_ZERO,
                (rs, i) -> new RuleStatsItemDto(
                        rs.getObject("rule_id", java.util.UUID.class),
                        rs.getLong("cnt")
                )
        );
    }

    public int delete(UUID ruleId) {
        return jdbcTemplate.update("DELETE FROM rule_stats WHERE rule_id = ?", ruleId);
    }
}
