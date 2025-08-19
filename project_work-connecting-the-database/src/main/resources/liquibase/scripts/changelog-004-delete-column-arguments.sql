--liquibase formatted sql

--changeset vladimirsa:006-drop-arguments-column-from-rule-conditions
ALTER TABLE rule_conditions DROP COLUMN arguments;