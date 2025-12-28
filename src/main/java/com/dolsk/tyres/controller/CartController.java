package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.CartDTO;
import com.dolsk.tyres.dto.CartItemDTO;
import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.CartItem;
import com.dolsk.tyres.service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // =========================
    // GET CART
    // =========================
    @GetMapping("/{userId}")
    public CartDTO getCart(@PathVariable Long userId) {
        return map(cartService.getCartByUserId(userId));
    }

    // =========================
    // ADD TO CART
    // =========================
    @PostMapping("/{userId}/add")
    public CartDTO addToCart(
            @PathVariable Long userId,
            @RequestParam Long tyreId,
            @RequestParam int quantity
    ) {
        return map(cartService.addTyreToCart(userId, tyreId, quantity));
    }

    // =========================
    // REMOVE ITEM
    // =========================
    @DeleteMapping("/{userId}/item/{itemId}")
    public CartDTO removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ) {
        return map(cartService.removeItem(userId, itemId));
    }

    // =========================
    // CLEAR CART
    // =========================
    @DeleteMapping("/{userId}/clear")
    public CartDTO clearCart(@PathVariable Long userId) {
        return map(cartService.clearCart(userId));
    }

    // =========================
    // MAPPER
    // =========================
    private CartDTO map(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getId());

        dto.setItems(
                cart.getItems().stream().map(this::mapItem).collect(Collectors.toList())
        );

        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getTyre().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotal(total);
        return dto;
    }

    private CartItemDTO mapItem(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setTyreId(item.getTyre().getId());
        dto.setTyreBrand(item.getTyre().getBrand());
        dto.setTyreSize(item.getTyre().getSize());
        dto.setPrice(item.getTyre().getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setImageUrl(item.getTyre().getImageUrl());
        return dto;
    }
}
