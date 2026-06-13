package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.AddToCartRequest;
import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.CartDTO;
import com.dolsk.tyres.dto.UpdateCartItemRequest;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Cart REST controller — production-grade, Amazon-style.
 *
 * Design principles:
 * - Thin controller: HTTP concerns only, no business logic
 * - User resolved from JWT — no userId in URLs (IDOR-proof)
 * - Request bodies for mutations (not query params)
 * - Proper HTTP status codes (200, 201, 404)
 * - Consistent ApiResponse envelope
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    /**
     * GET /api/cart
     * Returns the authenticated user's cart with all items and computed total.
     * Creates an empty cart automatically if the user doesn't have one.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        CartDTO cart = cartService.getCart(resolveUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    /**
     * POST /api/cart/items
     * Adds a tyre to the cart. If the tyre is already present, quantity is added.
     *
     * Request body: { "tyreId": 1, "quantity": 2 }
     * Returns: 201 Created with full cart state
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        CartDTO cart = cartService.addItem(
                resolveUserId(userDetails),
                request.getTyreId(),
                request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(cart, "Item added to cart"));
    }

    /**
     * PUT /api/cart/items/{itemId}
     * Updates the quantity of an existing cart item.
     * If quantity is 0, the item is removed.
     *
     * Request body: { "quantity": 3 }
     * Ownership validated — item must belong to this user's cart.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartDTO cart = cartService.updateItemQuantity(
                resolveUserId(userDetails),
                itemId,
                request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart item updated"));
    }

    /**
     * DELETE /api/cart/items/{itemId}
     * Removes a specific item from the cart.
     * Ownership validated — item must belong to this user's cart.
     * Returns 404 if item not found (handled by GlobalExceptionHandler).
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        CartDTO cart = cartService.removeItem(resolveUserId(userDetails), itemId);
        return ResponseEntity.ok(ApiResponse.ok(cart, "Item removed successfully"));
    }

    /**
     * DELETE /api/cart
     * Clears all items from the user's cart.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDTO>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        CartDTO cart = cartService.clearCart(resolveUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart cleared"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + userDetails.getUsername()));
        return user.getId();
    }
}
