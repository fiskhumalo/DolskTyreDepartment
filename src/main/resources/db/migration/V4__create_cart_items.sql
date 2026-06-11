CREATE TABLE cart_items (
     id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT,
    tyre_id BIGINT,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (tyre_id) REFERENCES tyres(id)
);