package com.dolsk.tyres.service.impl;

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

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final TyreRepository tyreRepository;
    // CartItemRepository removed — CartItem lifecycle is managed via Cart's
    // CascadeType.ALL + orphanRemoval, so direct repo access is not needed.

    @Override
    @Transactional
    public Cart getCartByUserId(Long userId) {
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

    @Override
    @Transactional
    public Cart addTyreToCart(Long userId, Long tyreId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Tyre tyre = tyreRepository.findById(tyreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tyre not found with id: " + tyreId));

        cart.getItems().stream()
                .filter(item -> item.getTyre().getId().equals(tyreId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setTyre(tyre);
                            newItem.setQuantity(quantity);
                            cart.getItems().add(newItem);
                        }
                );

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
