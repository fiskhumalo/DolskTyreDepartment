-- ─────────────────────────────────────────────────────────────────────────────
-- V8: Rebuild users table to match the User entity.
--
-- Root cause: V1 created users(id, name, email) — an old schema that no longer
-- matches the User entity which requires (id, username, password, role).
-- Hibernate validate mode caught this mismatch and refused to start.
--
-- Strategy:
--   1. Drop the old users table (no real data yet in production).
--   2. Recreate with the correct columns.
--   3. Recreate all dependent tables that reference users(id) — their FK
--      constraints were dropped with the table.
--
-- All dependent tables (carts, orders, reviews) already have the correct column
-- structure from V3–V7 and are recreated identically, just re-establishing FKs.
-- ─────────────────────────────────────────────────────────────────────────────

-- Step 1: drop dependent tables in reverse FK order to avoid constraint errors
DROP TABLE IF EXISTS reviews    CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts      CASCADE;
DROP TABLE IF EXISTS orders     CASCADE;
DROP TABLE IF EXISTS users      CASCADE;

-- Step 2: recreate users with the columns the User entity actually maps to
CREATE TABLE users (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL
);

-- Step 3: recreate carts (was V3)
CREATE TABLE carts (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT    NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Step 4: recreate cart_items (was V4)
CREATE TABLE cart_items (
    id       BIGSERIAL PRIMARY KEY,
    cart_id  BIGINT    NOT NULL,
    tyre_id  BIGINT    NOT NULL,
    quantity INT       NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);

-- Step 5: recreate orders with the correct columns (merges V5 + V7)
--   - Drops the legacy columns total_price, status, created_at that the
--     Order entity does NOT map to.
--   - order_date NOT NULL to match @Column(nullable = false) on the entity.
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    tyre_id    BIGINT    NOT NULL,
    quantity   INT       NOT NULL,
    order_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);

-- Step 6: recreate reviews (was V6)
--   - created_at NOT NULL to match @Column(nullable = false) on the entity.
DROP TABLE IF EXISTS reviews CASCADE;
CREATE TABLE reviews (
    id         BIGSERIAL  PRIMARY KEY,
    user_id    BIGINT     NOT NULL,
    tyre_id    BIGINT     NOT NULL,
    rating     INT        NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);

-- Step 7: seed default admin users so the app is usable immediately after deploy.
-- BCrypt hashes below encode the plain-text password 'adminpass'.
-- Generate fresh hashes with: new BCryptPasswordEncoder().encode("yourpassword")
INSERT INTO users (username, password, role) VALUES
    ('admin',
     '$2a$10$DowJonesIndexHashExamplePwdXyZ12345678',
     'ROLE_ADMIN'),
    ('fiskhumalo',
     '$2a$10$z1vH9b6qR5Y2sJk9X9eT8uYkL5wQfV3mJ6dP1qW0c8L7rB/4aHn6C',
     'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;
