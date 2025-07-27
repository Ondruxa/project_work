package ru.skypro.teamwork.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean userHasDebitProduct(UUID userId) {
        String sql = """
        SELECT COUNT(*) > 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT'
        """;
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        return Boolean.TRUE.equals(result);
    }

    public boolean userHasNoInvestProduct(UUID userId) {
        String sql = """
        SELECT COUNT(*) = 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'INVEST'
        """;
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        return Boolean.TRUE.equals(result);
    }

    public boolean userSavingTopUpOverThousand(UUID userId) {
        String sql = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
          AND p.type = 'SAVING'
          AND t.type = 'DEPOSIT'
        """;
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return result != null && result > 1000;
    }

    public int getRandomTransactionAmount(UUID user) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                user);
        return result != null ? result : 0;
    }
}
