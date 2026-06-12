-- ─────────────────────────────────────────────────────────────────────────────
-- V10: Set all admin passwords to 'Prodolsk7' with a valid BCrypt hash.
-- This replaces any broken/placeholder hashes from V8 and V9.
-- ─────────────────────────────────────────────────────────────────────────────

-- BCrypt hash of 'Prodolsk7'
UPDATE users
SET password = '$2a$10$Uhquz0.pASui7z0fMjRNWeocUwI1pyHsJ9jGRCXrYqWjTlUxq4R0u'
WHERE username IN ('admin', 'fiskhumalo');
