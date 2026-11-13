-- schema.sql - Creacion idempotente de la tabla units para PostgreSQL / RDS
CREATE TABLE IF NOT EXISTS units (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL,
    type            VARCHAR(50)     NOT NULL,
    capacity        INTEGER         NOT NULL,
    price_per_night NUMERIC(10,2)   NOT NULL,
    available       BOOLEAN         NOT NULL DEFAULT TRUE
);

