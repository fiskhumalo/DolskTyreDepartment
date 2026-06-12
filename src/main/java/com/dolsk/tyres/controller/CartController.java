package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.CartDTO;
import com.dolsk.tyres.dto.CartItemDTO;
import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.CartItem;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Cart endpoints — all operations are scoped to the authenticated user.
 *
 * The user is resolved from the JWT token (via @AuthenticationPrincipal),
 * NOT from a URL path parameter. This prevents cross-user data leakage.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // ── GET /api/cart — fetch authenticated user's cart ────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getMyCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.getCartByUserId(userId))));
    }

    // ── Legacy: GET /api/cart/{userId} — kept for backward compatibility ──────
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.getCartByUserId(userId))));
    }

    // ── POST /api/cart/items — add item to authenticated user's cart ───────────
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tyreId,
            @RequestParam(defaultValue = "1") int quantity) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.addTyreToCart(userId, tyreId, quantity))));
    }

    // ── Legacy: POST /api/cart/{userId}/add ────────────────────────────────────
    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @PathVariable Long userId,
            @RequestParam Long tyreId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.addTyreToCart(userId, tyreId, quantity))));
    }

    // ── PUT /api/cart/items/{itemId} — update item quantity ────────────────────
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.updateItemQuantity(userId, itemId, quantity))));
    }

    // ── DELETE /api/cart/items/{itemId} — remove item from cart ────────────────
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.removeItem(userId, itemId))));
    }

    // ── Legacy: DELETE /api/cart/{userId}/item/{itemId} ────────────────────────
    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItemLegacy(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(
                ApiResponse.ok(toDto(cartService.removeItem(userId, itemId))));
    }

    // ── DELETE /api/cart — clear entire cart ───────────────────────────────────
    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDTO>> clearMyCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.clearCart(userId))));
    }

    // ── Legacy: DELETE /api/cart/{userId}/clear ────────────────────────────────
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<CartDTO>> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(toDto(cartService.clearCart(userId))));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + userDetails.getUsername()));
        return user.getId();
    }

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
