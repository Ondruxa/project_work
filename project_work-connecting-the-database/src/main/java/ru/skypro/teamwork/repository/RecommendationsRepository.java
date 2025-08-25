package ru.skypro.teamwork.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Value;
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

    @Value
    private static class ProductTypeKey {
        UUID userId;
        String productType;
    }

    @Value
    private static class ActiveProductTypeKey {
        UUID userId;
        String productType;
        int minTransactions;
    }

    @Value
    private static class TransactionSumKey {
        UUID userId;
        String productType;
        String transactionType;
    }

    private final Cache<UUID, Boolean> debitProductCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> noInvestProductCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> savingTopUpOverThousandCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Integer> randomTransactionAmountCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> topUpOverFiftyThousandCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> debitTopUpGreaterThanSpendCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> noCreditProductCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<UUID, Boolean> debitSpendOverHundredThousandCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<ProductTypeKey, Boolean> productTypeCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<ActiveProductTypeKey, Boolean> activeProductTypeCache = Caffeine.newBuilder().maximumSize(10_000).build();
    private final Cache<TransactionSumKey, Integer> transactionSumCache = Caffeine.newBuilder().maximumSize(10_000).build();

    public boolean userHasDebitProduct(UUID userId) {
        return debitProductCache.get(userId, k -> {
            String sql = """
            SELECT COUNT(*) > 0
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = 'DEBIT'
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
            return Boolean.TRUE.equals(result);
        });
    }

    public boolean userHasNoInvestProduct(UUID userId) {
        return noInvestProductCache.get(userId, k -> {
            String sql = """
            SELECT COUNT(*) = 0
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = 'INVEST'
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
            return Boolean.TRUE.equals(result);
        });
    }

    public boolean userSavingTopUpOverThousand(UUID userId) {
        return savingTopUpOverThousandCache.get(userId, k -> {
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
        });
    }

    public int getRandomTransactionAmount(UUID user) {
        return randomTransactionAmountCache.get(user, k -> {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                    Integer.class,
                    user);
            return result != null ? result : 0;
        });
    }

    public boolean userHasTopUpOverFiftyThousand(UUID userId) {
        return topUpOverFiftyThousandCache.get(userId, k -> {
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
        });
    }

    public boolean debitTopUpGreaterThanDebitSpend(UUID userId) {
        return debitTopUpGreaterThanSpendCache.get(userId, k -> {
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
        });
    }

    public boolean userHasNoCreditProduct(UUID userId) {
        return noCreditProductCache.get(userId, k -> {
            String sql = """
            SELECT COUNT(*) = 0
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = 'CREDIT'
        """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
            return Boolean.TRUE.equals(result);
        });
    }

    public boolean debitSpendOverHundredThousand(UUID userId) {
        return debitSpendOverHundredThousandCache.get(userId, k -> {
            String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = 'DEBIT' AND t.type = 'WITHDRAW'
        """;
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
            return result != null && result > 100_000;
        });
    }

    public boolean userHasProductType(UUID userId, String productType) {
        ProductTypeKey key = new ProductTypeKey(userId, productType);
        return productTypeCache.get(key, k -> {
            String sql = """
                SELECT COUNT(*) > 0
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return Boolean.TRUE.equals(result);
        });
    }

    public boolean userHasActiveProductType(UUID userId, String productType, int minTransactions) {
        ActiveProductTypeKey key = new ActiveProductTypeKey(userId, productType, minTransactions);
        return activeProductTypeCache.get(key, k -> {
            String sql = """
                SELECT COUNT(*) >= ?
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
            """;
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, minTransactions, userId, productType);
            return Boolean.TRUE.equals(result);
        });
    }

    public int getTransactionSum(UUID userId, String productType, String transactionType) {
        TransactionSumKey key = new TransactionSumKey(userId, productType, transactionType);
        return transactionSumCache.get(key, k -> {
            String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ? AND t.type = ?
            """;
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType, transactionType);
            return result != null ? result : 0;
        });
    }
}