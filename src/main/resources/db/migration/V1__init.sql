CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE orders
(
    id             UUID PRIMARY KEY   DEFAULT gen_random_uuid(),

    comment        TEXT      NOT NULL,
    creation_date  TIMESTAMP NOT NULL DEFAULT now(),
    due_date       TIMESTAMP NOT NULL,

    order_number   INT       NOT NULL,

    accepted_by    UUID,
    assigned_to    UUID,

    price          INT       NOT NULL,

    payment_status TEXT      NOT NULL,
    partially_paid INT,
    payment_method TEXT      NOT NULL,

    status         TEXT      NOT NULL
);

CREATE INDEX idx_orders_order_number ON orders (order_number);
CREATE INDEX idx_orders_creation_date ON orders (creation_date);


CREATE TABLE users
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),

    username   TEXT      NOT NULL UNIQUE,
    password   TEXT      NOT NULL,

    first_name TEXT      NOT NULL,
    last_name  TEXT      NOT NULL,

    role       TEXT      NOT NULL,
    status     TEXT      NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_accepted_by
        FOREIGN KEY (accepted_by) REFERENCES users (id),

    ADD CONSTRAINT fk_orders_assigned_to
        FOREIGN KEY (assigned_to) REFERENCES users (id);

-- admin/admin
INSERT INTO users (username, password, first_name, last_name, status, role)
VALUES ('admin',
        '{bcrypt}$2a$10$nkyAjGFOeQ8NWr1pd1ooR.DPBdpikX.cUK6WE00eUlh/cdwOiu6hK',
        'admin',
        'admin',
        'ACTIVE',
        'ADMIN');
