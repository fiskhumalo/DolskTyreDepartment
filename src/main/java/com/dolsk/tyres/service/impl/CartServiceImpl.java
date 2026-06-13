package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.CartDTO;
import com.dolsk.tyres.dto.CartItemDTO;
import com.dolsk.tyres.exception.ResourceNotFoundException;
import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.CartItem;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.CartRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Production-grade cart service.
 *
 * Key design decisions:
 * - One cart per user, auto-created on first access
 * - One cart_item per tyre per cart (duplicates merge by adding quantity)
 * - Total is always computed dynamically (price × quantity per item)
 * - All operations validate ownership: items must belong to the user's cart
 * - Returns DTOs — entities never escape the service layer
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final TyreRepository tyreRepository;

    // ── Public API ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(Long userId) {
        return toDto(getOrCreateCart(userId));
    }

    @Override
    @Transactional
    public CartDTO addItem(Long userId, Long tyreId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Tyre tyre = tyreRepository.findById(tyreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tyre not found with id: " + tyreId));

        // Duplicate prevention: if this tyre is already in the cart, add to quantity
        cart.getItems().stream()
                .filter(item -> item.getTyre().getId().equals(tyreId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setTyre(tyre);
                            newItem.setQuantity(quantity);
                            cart.getItems().add(newItem);
                        }
                );

        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartDTO updateItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = findOwnedItem(cart, itemId);

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartDTO removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = findOwnedItem(cart, itemId);
        cart.getItems().remove(item);
        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartDTO clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        return toDto(cartRepository.save(cart));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Gets the user's cart, or creates an empty one if it doesn't exist.
     * This ensures every authenticated user always has exactly one cart.
     */
    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    /**
     * Finds an item within the user's cart.
     * Guarantees ownership: the item must belong to THIS user's cart.
     * Throws 404 if the item doesn't exist or belongs to another user.
     */
    private CartItem findOwnedItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + itemId));
    }

    // ── DTO Mapping ───────────────────────────────────────────────────────────

    private CartDTO toDto(Cart cart) {
        var items = cart.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemDTO::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(cart.getId(), items.size(), items, total);
    }

    private CartItemDTO toItemDto(CartItem item) {
        BigDecimal unitPrice = item.getTyre().getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemDTO(
                item.getId(),
                item.getTyre().getId(),
                item.getTyre().getBrand(),
                item.getTyre().getSize(),
                unitPrice,
                item.getQuantity(),
                lineTotal,
                item.getTyre().getImageUrl()
        );
    }
}
