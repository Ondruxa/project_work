CREATE TABLE dynamic_rules (
    id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_text VARCHAR(2000),
    rule TEXT NOT NULL
);