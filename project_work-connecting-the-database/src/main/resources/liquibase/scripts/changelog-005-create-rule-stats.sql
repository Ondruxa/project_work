--liquibase formatted sql

--changeset vladimirsa:005-create-rule-stats
CREATE TABLE IF NOT EXISTS rule_stats (
    rule_id UUID PRIMARY KEY,
    count INT DEFAULT 0 NOT NULL,
    CONSTRAINT fk_rule_id FOREIGN KEY (rule_id) REFERENCES dynamic_rules(id) ON DELETE CASCADE
);
