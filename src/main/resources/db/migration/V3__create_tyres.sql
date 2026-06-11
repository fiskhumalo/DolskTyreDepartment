CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    tyre_id BIGINT,
    quantity INT,
    status VARCHAR(50)
);