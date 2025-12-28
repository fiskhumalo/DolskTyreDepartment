package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.OrderDTO;
import java.util.List;

public interface OrderService {
  OrderDTO placeOrder(OrderDTO dto);
  List<OrderDTO> getOrdersForUser(String username);
  boolean delete(Long id);
}