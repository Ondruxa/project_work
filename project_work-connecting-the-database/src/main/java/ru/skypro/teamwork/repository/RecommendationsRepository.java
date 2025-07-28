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

    public boolean userHasTopUpOverFiftyThousand(UUID userId) {
        String sql = """
        SELECT 
            SUM(CASE WHEN p.type = 'DEBIT' AND t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) AS debit_sum,
            SUM(CASE WHEN p.type = 'SAVING' AND t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) AS saving_sum
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
    """;
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                int debitSum = rs.getInt("debit_sum");
                int savingSum = rs.getInt("saving_sum");
                return debitSum >= 50_000 || savingSum >= 50_000;
            }
            return false;
        }, userId);
    }

    public boolean debitTopUpGreaterThanDebitSpend(UUID userId) {
        String sql = """
        SELECT
            COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount END), 0) AS topup,
            COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount END), 0) AS spend
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT'
    """;
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                int topup = rs.getInt("topup");
                int spend = rs.getInt("spend");
                return topup > spend;
            }
            return false;
        }, userId);
    }

    public boolean userHasNoCreditProduct(UUID userId) {
        String sql = """
        SELECT COUNT(*) = 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'CREDIT'
    """;
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        return Boolean.TRUE.equals(result);
    }

    public boolean debitSpendOverHundredThousand(UUID userId) {
        String sql = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT' AND t.type = 'WITHDRAW'
    """;
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return result != null && result > 100_000;
    }
}
