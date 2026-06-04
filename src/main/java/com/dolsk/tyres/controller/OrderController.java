package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order endpoints.
 *
 * - Any authenticated user: place orders, view their own orders.
 * - ROLE_ADMIN: view ALL orders, delete any order.
 *
 * Username is resolved from @AuthenticationPrincipal — the incoming DTO
 * is never mutated by the controller.
 *
 * Admin/user branching is enforced via @PreAuthorize, NOT inside the service.
 *
 * No try/catch — exceptions propagate to GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(
            @Valid @RequestBody OrderDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Pass username separately — DTO is never mutated
        OrderDTO result = orderService.placeOrder(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.ok(orderService.getOrdersForUser(userDetails.getUsername())));
    }

    /** Admin only: returns all orders across all users. */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrders()));
    }

    /** Admin only: delete any order by ID. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Order with id " + id + " has been successfully deleted"));
    }
}
