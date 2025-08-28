package ru.skypro.teamwork.repository.sql;

public final class RecommendationSql {

    private RecommendationSql() {}

    public static final String USER_HAS_DEBIT_PRODUCT = """
        SELECT COUNT(*) > 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT'
        """;

    public static final String USER_HAS_NO_INVEST_PRODUCT = """
        SELECT COUNT(*) = 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'INVEST'
        """;

    public static final String SAVING_TOPUP_SUM = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
          AND p.type = 'SAVING'
          AND t.type = 'DEPOSIT'
        """;

    public static final String RANDOM_TRANSACTION_AMOUNT = """
        SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1
        """;

    public static final String TOPUP_OVER_FIFTY_THOUSAND = """
        SELECT
            SUM(CASE WHEN p.type = 'DEBIT'  AND t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) AS debit_sum,
            SUM(CASE WHEN p.type = 'SAVING' AND t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) AS saving_sum
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
        """;

    public static final String DEBIT_TOPUP_VS_SPEND = """
        SELECT
            COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT'  THEN t.amount END), 0) AS topup,
            COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount END), 0) AS spend
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT'
        """;

    public static final String USER_HAS_NO_CREDIT_PRODUCT = """
        SELECT COUNT(*) = 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'CREDIT'
        """;

    public static final String DEBIT_SPEND_SUM = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = 'DEBIT' AND t.type = 'WITHDRAW'
        """;

    public static final String USER_HAS_PRODUCT_TYPE = """
        SELECT COUNT(*) > 0
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = ?
        """;

    public static final String USER_HAS_ACTIVE_PRODUCT_TYPE = """
        SELECT COUNT(*) >= ?
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = ?
        """;

    public static final String TRANSACTION_SUM = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = ? AND t.type = ?
        """;
}