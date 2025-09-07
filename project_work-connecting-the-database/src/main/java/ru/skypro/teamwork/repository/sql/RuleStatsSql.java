package ru.skypro.teamwork.repository.sql;

public final class RuleStatsSql {
    private RuleStatsSql() {}

    public static final String UPSERT_INCREMENT = """
        INSERT INTO rule_stats (rule_id, count)
        VALUES (?, 1)
        ON CONFLICT (rule_id) DO UPDATE SET count = rule_stats.count + 1;
    """;

    public static final String FIND_ALL_WITH_ZERO = """
        SELECT
               r.id AS id,
               r.id AS rule_id,
               COALESCE(rs.count, 0) AS cnt
        FROM dynamic_rules r
        LEFT JOIN rule_stats rs ON r.id = rs.rule_id
        ORDER BY r.id
        """;
}