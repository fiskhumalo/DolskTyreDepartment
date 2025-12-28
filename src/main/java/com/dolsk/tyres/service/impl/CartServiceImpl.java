package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.CartItem;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.CartItemRepository;
import com.dolsk.tyres.repository.CartRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final UserRepository userRepo;
    private final TyreRepository tyreRepo;

    // =========================
    // GET OR CREATE CART
    // =========================
    @Override
    public Cart getCartByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepo.save(cart);
                });
    }

    // =========================
    // ADD TYRE TO CART
    // =========================
    @Override
    public Cart addTyreToCart(Long userId, Long tyreId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Tyre tyre = tyreRepo.findById(tyreId)
                .orElseThrow(() -> new RuntimeException("Tyre not found"));

        Optional<CartItem> existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getTyre().getId().equals(tyreId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setTyre(tyre);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }

        return cartRepo.save(cart);
    }

    // =========================
    // REMOVE ITEM
    // =========================
    @Override
    public Cart removeItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        return cartRepo.save(cart);
    }

    // =========================
    // CLEAR CART
    // =========================
    @Override
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        return cartRepo.save(cart);
    }
}

