CREATE TABLE carts (
     id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);