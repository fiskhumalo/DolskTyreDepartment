package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.CartDTO;
import com.dolsk.tyres.dto.CartItemDTO;
import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.CartItem;
import com.dolsk.tyres.service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Cart endpoints.
 *
 * Mapping is handled here because CartService returns Cart entities directly
 * (the Cart module doesn't have a dedicated service-level mapper yet).
 * When CartService is refactored to return CartDTO, move the mapping there.
 *
 * No try/catch — exceptions propagate to GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.getCartByUserId(userId))));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @PathVariable Long userId,
            @RequestParam Long tyreId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.addTyreToCart(userId, tyreId, quantity))));
    }

    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.removeItem(userId, itemId))));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<CartDTO>> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.clearCart(userId))));
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private CartDTO toDto(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getId());
        dto.setItems(cart.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList()));
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getTyre().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotal(total);
        return dto;
    }

    private CartItemDTO toItemDto(CartItem item) {
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
