--liquibase formatted sql

--changeset vladimirsa:001-create-dynamic-rules
CREATE TABLE dynamic_rules (
    id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id UUID NOT NULL,
    product_text VARCHAR(2000)
);