-- Seed default admin user with bcrypt password 'adminpass'

-- Note: replace hash above with output of BCryptPasswordEncoder.encode("adminpass").
-- Admin user

INSERT INTO users (username, password, role)
VALUES
('admin', '$2a$10$DowJonesIndexHashExamplePwdXyZ12345678', 'ROLE_ADMIN'),
('fiskhumalo', '$2a$10$z1vH9b6qR5Y2sJk9X9eT8uYkL5wQfV3mJ6dP1qW0c8L7rB/4aHn6C', 'ROLE_ADMIN'),
('johndoe', '$2a$10$k3Gf7Lh8sY9q1P5rZ2eT9uVjL5wQfV3mJ6dP1qW0c8L7rB/4aHn6C', 'ROLE_USER');



-- Sample tyres
INSERT INTO tyres (id, brand, size, price) VALUES (1, 'Michelin', '205/55R16', 1200.00);
INSERT INTO tyres (id, brand, size, price) VALUES (2, 'Pirelli', '215/60R16', 1400.00);
INSERT INTO orders (order_date, quantity, tyre_id, user_id) VALUES
('2025-12-01 09:30:00', 2, 1, 1),
('2025-12-01 10:45:00', 1, 2, 1),
('2025-12-02 14:15:00', 4, 1, 1),
('2025-12-02 15:00:00', 3, 2, 1),
('2025-12-03 11:20:00', 1, 1, 1),
('2025-12-03 12:50:00', 2, 2, 1),
('2025-12-03 16:30:00', 5, 1, 1),
('2025-12-04 09:00:00', 2, 2, 1),
('2025-12-04 10:45:00', 1, 1, 1),
('2025-12-04 14:15:00', 3, 2, 1);
