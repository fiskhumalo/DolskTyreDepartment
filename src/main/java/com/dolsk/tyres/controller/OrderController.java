package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.dto.PagedResponse;
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
 * - ROLE_ADMIN: view ALL orders (paginated), delete any order.
 *
 * Supports both:
 *   GET /api/orders/all         → all orders unpaginated (backward compatible)
 *   GET /api/orders/all/paged   → paginated with page/size params
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
        OrderDTO result = orderService.placeOrder(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.ok(orderService.getOrdersForUser(userDetails.getUsername())));
    }

    /** Admin only: returns all orders (backward compatible, unpaginated). */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrders()));
    }

    /**
     * Admin only: paginated all orders.
     * GET /api/orders/all/paged?page=0&size=20
     */
    @GetMapping("/all/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> listAllOrdersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrdersPaged(page, safeSize)));
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
