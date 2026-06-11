CREATE TABLE reviews (
     id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    tyre_id BIGINT,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);