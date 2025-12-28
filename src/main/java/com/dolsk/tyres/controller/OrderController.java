package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(@Validated @RequestBody OrderDTO dto, Principal principal) {
    dto.setUsername(principal.getName());
    OrderDTO result = orderService.placeOrder(dto);
    return ResponseEntity.ok(new ApiResponse<>(true, result, null));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderDTO>>> listOrders(Principal principal) {
    List<OrderDTO> orders = orderService.getOrdersForUser(principal.getName());
    return ResponseEntity.ok(new ApiResponse<>(true, orders, null));


  }
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
    boolean deleted = orderService.delete(id);

    if (!deleted) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ApiResponse<>(false, null, "Order not found"));
    }

    return ResponseEntity.ok(new ApiResponse<>(true, null,"Order with id " + id + " has been successfully deleted"));
  }

}