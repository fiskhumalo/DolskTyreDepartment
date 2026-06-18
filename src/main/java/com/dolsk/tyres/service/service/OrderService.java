package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.dto.PagedResponse;

import java.util.List;

public interface OrderService {

    OrderDTO placeOrder(OrderDTO dto, String username);

    /** Returns all orders for the authenticated user (unpaginated — backward compatible). */
    List<OrderDTO> getOrdersForUser(String username);

    /** Returns all orders across all users (unpaginated — backward compatible). */
    List<OrderDTO> getAllOrders();

    /** Admin: paginated all orders. */
    PagedResponse<OrderDTO> getAllOrdersPaged(int page, int size);

    void delete(Long id);
}
