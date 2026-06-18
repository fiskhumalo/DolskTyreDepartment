-- ─────────────────────────────────────────────────────────────────────────────
-- V12: Add missing indexes on FK columns and unique constraint on cart_items.
--
-- Why: PostgreSQL only auto-creates indexes on PRIMARY KEY and UNIQUE columns.
-- FK columns used in WHERE/JOIN clauses need explicit indexes for performance.
-- Without them, queries degrade to full table scans as data grows.
--
-- Safe: CREATE INDEX IF NOT EXISTS is idempotent.
-- Safe: Adding indexes does NOT lock tables for reads on PostgreSQL.
-- ─────────────────────────────────────────────────────────────────────────────

-- ── orders table ──────────────────────────────────────────────────────────────
-- Used by: OrderRepository.findAllByUser() → WHERE user_id = ?
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);

-- Used by: OrderRepository.existsByTyreId() → WHERE tyre_id = ?
CREATE INDEX IF NOT EXISTS idx_orders_tyre_id ON orders(tyre_id);

-- ── cart_items table ──────────────────────────────────────────────────────────
-- Used by: Cart.items (OneToMany mapped by cart_id) → WHERE cart_id = ?
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);

-- Used by: JOIN on tyre data when loading cart items
CREATE INDEX IF NOT EXISTS idx_cart_items_tyre_id ON cart_items(tyre_id);

-- ── Unique constraint: one cart item per tyre per cart ─────────────────────────
-- Prevents race conditions from creating duplicate (cart_id, tyre_id) rows.
-- The application already merges duplicates in Java code, but this is the
-- database-level safety net. If a duplicate somehow slips through, the DB
-- rejects it with a constraint violation instead of silently corrupting data.
--
-- NOTE: If duplicate rows already exist, this will fail. The V8 migration
-- recreated these tables fresh, so no duplicates should exist.
ALTER TABLE cart_items
    ADD CONSTRAINT uq_cart_items_cart_tyre UNIQUE (cart_id, tyre_id);

-- ── reviews table (for future use) ───────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_tyre_id ON reviews(tyre_id);
