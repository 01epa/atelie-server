CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE orders
(
    id             UUID PRIMARY KEY       DEFAULT gen_random_uuid(),

    comment        VARCHAR(3000) NOT NULL,
    creation_date  TIMESTAMP     NOT NULL DEFAULT now(),
    due_date       TIMESTAMP     NOT NULL,

    order_number   INT           NOT NULL,

    accepted_by    UUID,
    assigned_to    UUID,

    price          INT           NOT NULL,

    payment_status VARCHAR(50)   NOT NULL,
    partially_paid INT,
    payment_method VARCHAR(50)   NOT NULL,

    status         VARCHAR(50)   NOT NULL
);

CREATE INDEX idx_orders_order_number ON orders (order_number);
CREATE INDEX idx_orders_creation_date ON orders (creation_date);


CREATE TABLE users
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),

    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,

    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,

    role       VARCHAR(255) NOT NULL,
    active     BOOLEAN               DEFAULT true,

    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_accepted_by
        FOREIGN KEY (accepted_by) REFERENCES users (id),

    ADD CONSTRAINT fk_orders_assigned_to
        FOREIGN KEY (assigned_to) REFERENCES users (id);

-- admin/admin
INSERT INTO users (username, password, role, first_name, last_name)
VALUES ('admin',
        '{bcrypt}$2a$10$nkyAjGFOeQ8NWr1pd1ooR.DPBdpikX.cUK6WE00eUlh/cdwOiu6hK',
        'ADMIN',
        'admin',
        'admin');

INSERT INTO users (username, password, role, first_name, last_name)
VALUES ('user',
        '{bcrypt}$2a$10$nkyAjGFOeQ8NWr1pd1ooR.DPBdpikX.cUK6WE00eUlh/cdwOiu6hK',
        'USER',
        'a',
        'b');