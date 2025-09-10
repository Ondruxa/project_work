package ru.skypro.teamwork.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skypro.teamwork.repository.sql.RecommendationSql;

import java.util.UUID;

/**
 * Репозиторий данных рекомендаций, работающий поверх read-only JDBC источника.
 * <p>
 * Содержит набор кэширующих методов для снижения нагрузки на БД при многократных запросах правил.
 * Использует локальные Caffeine-кэши.
 */
@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate специализированный JdbcTemplate для БД рекомендаций
     */
    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value
    private static class ProductTypeKey { UUID userId; String productType; }

    @Value
    private static class ActiveProductTypeKey { UUID userId; String productType; int minTransactions; }

    @Value
    private static class TransactionSumKey { UUID userId; String productType; String transactionType; }

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

    /**
     * Проверяет наличие дебетового продукта у пользователя.
     * @param userId идентификатор пользователя
     * @return true если есть хотя бы один дебетовый продукт
     */
    public boolean userHasDebitProduct(UUID userId) {
        return debitProductCache.get(userId,
                k -> Boolean.TRUE.equals(jdbcTemplate.queryForObject(RecommendationSql.USER_HAS_DEBIT_PRODUCT, Boolean.class, userId)));
    }

    /**
     * Проверяет, что у пользователя отсутствуют инвестиционные продукты.
     * @param userId идентификатор пользователя
     * @return true если нет инвест продуктов
     */
    public boolean userHasNoInvestProduct(UUID userId) {
        return noInvestProductCache.get(userId,
                k -> Boolean.TRUE.equals(jdbcTemplate.queryForObject(RecommendationSql.USER_HAS_NO_INVEST_PRODUCT, Boolean.class, userId)));
    }

    /**
     * Проверяет, превышает ли сумма пополнений сбережений порог 1000.
     * @param userId идентификатор пользователя
     * @return true если сумма больше 1000
     */
    public boolean userSavingTopUpOverThousand(UUID userId) {
        return savingTopUpOverThousandCache.get(userId, k -> {
            Integer sum = jdbcTemplate.queryForObject(RecommendationSql.SAVING_TOPUP_SUM, Integer.class, userId);
            return sum != null && sum > 1_000;
        });
    }

    /**
     * Возвращает случайную сумму транзакции (предподготовленные данные).
     * @param user идентификатор пользователя
     * @return сумма
     */
    public int getRandomTransactionAmount(UUID user) {
        return randomTransactionAmountCache.get(user,
                k -> jdbcTemplate.queryForObject(RecommendationSql.RANDOM_TRANSACTION_AMOUNT, Integer.class, user));
    }

    /**
     * Проверяет наличие пополнений свыше 50к по дебету либо сбережению.
     * @param userId идентификатор пользователя
     * @return true если условие выполняется
     */
    public boolean userHasTopUpOverFiftyThousand(UUID userId) {
        return topUpOverFiftyThousandCache.get(userId, k ->
                jdbcTemplate.query(RecommendationSql.TOPUP_OVER_FIFTY_THOUSAND, rs -> {
                    if (rs.next()) {
                        int debitSum = rs.getInt("debit_sum");
                        int savingSum = rs.getInt("saving_sum");
                        return debitSum >= 50_000 || savingSum >= 50_000;
                    }
                    return false;
                }, userId));
    }

    /**
     * Сравнивает общие суммы пополнений и списаний по дебету.
     * @param userId идентификатор пользователя
     * @return true если пополнения больше списаний
     */
    public boolean debitTopUpGreaterThanDebitSpend(UUID userId) {
        return debitTopUpGreaterThanSpendCache.get(userId, k ->
                jdbcTemplate.query(RecommendationSql.DEBIT_TOPUP_VS_SPEND, rs -> {
                    if (rs.next()) {
                        int topup = rs.getInt("topup");
                        int spend = rs.getInt("spend");
                        return topup > spend;
                    }
                    return false;
                }, userId));
    }

    /**
     * Проверяет отсутствие кредитного продукта.
     * @param userId идентификатор пользователя
     * @return true если нет кредитного продукта
     */
    public boolean userHasNoCreditProduct(UUID userId) {
        return noCreditProductCache.get(userId,
                k -> Boolean.TRUE.equals(jdbcTemplate.queryForObject(RecommendationSql.USER_HAS_NO_CREDIT_PRODUCT, Boolean.class, userId)));
    }

    /**
     * Проверяет превышает ли расход по дебетовым транзакциям 100000.
     * @param userId идентификатор пользователя
     * @return true если сумма расхода больше порога
     */
    public boolean debitSpendOverHundredThousand(UUID userId) {
        return debitSpendOverHundredThousandCache.get(userId, k -> {
            Integer sum = jdbcTemplate.queryForObject(RecommendationSql.DEBIT_SPEND_SUM, Integer.class, userId);
            return sum != null && sum > 100_000;
        });
    }

    /**
     * Проверяет наличие продукта конкретного типа.
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @return true если есть продукт указанного типа
     */
    public boolean userHasProductType(UUID userId, String productType) {
        ProductTypeKey key = new ProductTypeKey(userId, productType);
        return productTypeCache.get(key, k ->
                Boolean.TRUE.equals(jdbcTemplate.queryForObject(RecommendationSql.USER_HAS_PRODUCT_TYPE, Boolean.class, userId, productType)));
    }

    /**
     * Проверяет наличие активного продукта типа (минимум транзакций).
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @param minTransactions минимальное количество транзакций
     * @return true если условие активности выполнено
     */
    public boolean userHasActiveProductType(UUID userId, String productType, int minTransactions) {
        ActiveProductTypeKey key = new ActiveProductTypeKey(userId, productType, minTransactions);
        return activeProductTypeCache.get(key, k ->
                Boolean.TRUE.equals(jdbcTemplate.queryForObject(RecommendationSql.USER_HAS_ACTIVE_PRODUCT_TYPE, Boolean.class,
                        minTransactions, userId, productType)));
    }

    /**
     * Возвращает сумму транзакций по типу продукта и типу операции.
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @param transactionType тип транзакции (например SPEND / TOPUP)
     * @return суммарное значение (0 если нет записей)
     */
    public int getTransactionSum(UUID userId, String productType, String transactionType) {
        TransactionSumKey key = new TransactionSumKey(userId, productType, transactionType);
        return transactionSumCache.get(key, k -> {
            Integer sum = jdbcTemplate.queryForObject(RecommendationSql.TRANSACTION_SUM, Integer.class,
                    userId, productType, transactionType);
            return sum != null ? sum : 0;
        });
    }

    /**
     * Инвалидирует все локальные кэши (используется эндпоинтом очистки).
     */
    public void clearCaches() {
        debitProductCache.invalidateAll();
        noInvestProductCache.invalidateAll();
        savingTopUpOverThousandCache.invalidateAll();
        randomTransactionAmountCache.invalidateAll();
        topUpOverFiftyThousandCache.invalidateAll();
        debitTopUpGreaterThanSpendCache.invalidateAll();
        noCreditProductCache.invalidateAll();
        debitSpendOverHundredThousandCache.invalidateAll();
        productTypeCache.invalidateAll();
        activeProductTypeCache.invalidateAll();
        transactionSumCache.invalidateAll();
    }
}