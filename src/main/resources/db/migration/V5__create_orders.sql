CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    tyre_id BIGINT,
    quantity INT,
    total_price NUMERIC(10,2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);