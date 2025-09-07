--liquibase formatted sql

--changeset vladimirsa:002-unique-product-id
CREATE UNIQUE INDEX ux_dynamic_rules_product_id ON dynamic_rules(product_id);

CREATE TABLE rule_conditions (
    id UUID PRIMARY KEY,
    dynamic_rule_id UUID NOT NULL,
    query VARCHAR(2000) NOT NULL,
    arguments TEXT,
    negate BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_rule_conditions_dynamic_rule
        FOREIGN KEY (dynamic_rule_id) REFERENCES dynamic_rules(id) ON DELETE CASCADE
);

CREATE INDEX idx_rule_conditions_dynamic_rule_id ON rule_conditions(dynamic_rule_id);
