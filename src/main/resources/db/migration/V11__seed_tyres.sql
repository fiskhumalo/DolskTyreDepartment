-- ─────────────────────────────────────────────────────────────────────────────
-- V11: Seed sample tyres into the catalogue.
-- Uses WHERE NOT EXISTS to be idempotent if re-run manually.
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Michelin', '205/55R16', 1200.00, 'Michelin Primacy 4 – excellent wet grip and longevity', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Michelin' AND size = '205/55R16');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Pirelli', '215/60R16', 1400.00, 'Pirelli Cinturato P7 – balanced performance tyre', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Pirelli' AND size = '215/60R16');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Continental', '195/65R15', 1100.00, 'Continental EcoContact 6 – fuel-efficient and quiet', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Continental' AND size = '195/65R15');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Bridgestone', '225/45R17', 1600.00, 'Bridgestone Turanza T005 – premium comfort tyre', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Bridgestone' AND size = '225/45R17');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Goodyear', '205/55R16', 1050.00, 'Goodyear EfficientGrip – affordable all-rounder', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Goodyear' AND size = '205/55R16');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Hankook', '185/65R15', 850.00, 'Hankook Ventus Prime 3 – great value for e-hailing drivers', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Hankook' AND size = '185/65R15');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Dunlop', '195/50R15', 950.00, 'Dunlop Sport BluResponse – sporty handling', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Dunlop' AND size = '195/50R15');

INSERT INTO tyres (brand, size, price, description, image_url)
SELECT 'Yokohama', '225/40R18', 1800.00, 'Yokohama Advan Sport V105 – high-performance', NULL
WHERE NOT EXISTS (SELECT 1 FROM tyres WHERE brand = 'Yokohama' AND size = '225/40R18');
