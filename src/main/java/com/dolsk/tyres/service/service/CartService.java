package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.CartDTO;

/**
 * Cart service interface.
 * All methods resolve the cart from the authenticated user's ID.
 * DTOs are returned — entities never leak to the controller layer.
 */
public interface CartService {

    /**
     * Returns the authenticated user's cart. Creates an empty cart if none exists.
     */
    CartDTO getCart(Long userId);

    /**
     * Adds a tyre to the user's cart.
     * If the tyre already exists in the cart, the quantity is incremented (not duplicated).
     *
     * @return the full updated cart
     */
    CartDTO addItem(Long userId, Long tyreId, int quantity);

    /**
     * Sets the quantity of an existing cart item.
     * If quantity is 0, the item is removed.
     * Validates that the item belongs to the user's cart (ownership check).
     *
     * @return the full updated cart
     */
    CartDTO updateItemQuantity(Long userId, Long itemId, int quantity);

    /**
     * Removes a specific item from the user's cart.
     * Validates ownership before removing.
     *
     * @return the full updated cart
     */
    CartDTO removeItem(Long userId, Long itemId);

    /**
     * Removes all items from the user's cart.
     *
     * @return the empty cart
     */
    CartDTO clearCart(Long userId);
}
