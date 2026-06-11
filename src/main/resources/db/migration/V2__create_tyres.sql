CREATE TABLE tyres (
   id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    size VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    description TEXT,
    image_url TEXT
);