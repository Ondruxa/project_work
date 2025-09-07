--liquibase formatted sql

--changeset vladimirsa:006-alter-rule-stats
CREATE TABLE IF NOT EXISTS rule_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID NOT NULL UNIQUE,
    count INT DEFAULT 0 NOT NULL,
    CONSTRAINT fk_rule_id FOREIGN KEY (rule_id) REFERENCES dynamic_rules(id) ON DELETE CASCADE
);
