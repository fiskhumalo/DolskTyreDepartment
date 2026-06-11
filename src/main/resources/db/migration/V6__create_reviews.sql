CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    tyre_id BIGINT,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);