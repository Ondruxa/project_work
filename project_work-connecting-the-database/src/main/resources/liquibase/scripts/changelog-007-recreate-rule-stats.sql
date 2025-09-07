--liquibase formatted sql

--changeset vladimirsa:007-recreate-rule-stats
DROP TABLE IF EXISTS rule_stats;
CREATE TABLE rule_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID NOT NULL UNIQUE,
    count INT DEFAULT 0 NOT NULL,
    CONSTRAINT fk_rule_id FOREIGN KEY (rule_id) REFERENCES dynamic_rules(id) ON DELETE CASCADE
);
