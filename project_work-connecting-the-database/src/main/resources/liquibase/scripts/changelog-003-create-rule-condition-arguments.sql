--liquibase formatted sql

--changeset vladimirsa:005-create-rule-condition-arguments
CREATE TABLE rule_condition_arguments (
    id UUID PRIMARY KEY,
    rule_condition_id UUID NOT NULL,
    argument TEXT,
    CONSTRAINT fk_rule_condition_arguments_rule_condition
        FOREIGN KEY (rule_condition_id) REFERENCES rule_conditions(id) ON DELETE CASCADE
);