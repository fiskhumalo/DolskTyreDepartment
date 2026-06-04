package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    /**
     * Places an order on behalf of the authenticated user.
     *
     * @param dto      contains tyreId and quantity — never the username
     * @param username resolved from the authenticated Principal by the controller
     */
    OrderDTO placeOrder(OrderDTO dto, String username);

    /** Returns only the orders belonging to the given user. */
    List<OrderDTO> getOrdersForUser(String username);

    /** Admin: returns all orders across all users. */
    List<OrderDTO> getAllOrders();

    /**
     * Deletes an order by ID.
     * @throws com.dolsk.tyres.exception.ResourceNotFoundException if not found
     */
    void delete(Long id);
}
