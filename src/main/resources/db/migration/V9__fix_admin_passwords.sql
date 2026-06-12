-- ─────────────────────────────────────────────────────────────────────────────
-- V9: Fix admin passwords with real BCrypt hashes.
-- V8 used placeholder hashes that BCryptPasswordEncoder cannot verify.
-- Password for both accounts: adminpass
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE users SET password = '$2a$10$H/JAhuWPjZwso43J6hwD..zJNQd9BXvJOmTxnzetnsfSUsLBbXRH6'
WHERE username = 'admin';

UPDATE users SET password = '$2a$10$H/JAhuWPjZwso43J6hwD..zJNQd9BXvJOmTxnzetnsfSUsLBbXRH6'
WHERE username = 'fiskhumalo';
