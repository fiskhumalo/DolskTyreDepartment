package com.dolsk.tyres.service.service;

import com.dolsk.tyres.model.Cart;

public interface CartService {

    Cart getCartByUserId(Long userId);

    Cart addTyreToCart(Long userId, Long tyreId, int quantity);

    Cart updateItemQuantity(Long userId, Long cartItemId, int quantity);

    Cart removeItem(Long userId, Long cartItemId);

    Cart clearCart(Long userId);
}
