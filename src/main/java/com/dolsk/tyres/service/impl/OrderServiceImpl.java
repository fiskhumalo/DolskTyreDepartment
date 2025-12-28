package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.model.Order;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.OrderRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepo;
  private final TyreRepository tyreRepo;
  private final UserRepository userRepo;

  private OrderDTO map(Order o) {
    return new OrderDTO(o.getId(), o.getTyre().getId(), o.getQuantity(), o.getUser().getUsername());
  }

  @Override
  public OrderDTO placeOrder(OrderDTO dto) {
    User u = userRepo.findByUsername(dto.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    Tyre t = tyreRepo.findById(dto.getTyreId())
            .orElseThrow(() -> new RuntimeException("Tyre not found"));
    Order o = new Order(null, u, t, dto.getQuantity(), LocalDateTime.now());
    o = orderRepo.save(o);
    return map(o);
  }


@Override
public List<OrderDTO> getOrdersForUser(String username) {
  User u = userRepo.findByUsername(username)
          .orElseThrow(() -> new RuntimeException("User not found"));
  List<Order> orders;

  // Check if admin
  if ("ROLE_ADMIN".equals(u.getRole())) {
    // Admin sees all orders
    orders = orderRepo.findAll();
  } else {
    // Regular users see only their orders
    orders = orderRepo.findAllByUser(u);
  }

  // Map and return the correct list
  return orders.stream()
          .map(this::map)
          .collect(Collectors.toList());
   }

  @Override
  public boolean delete(Long id) {
    if (!orderRepo.existsById(id)) return false;

    orderRepo.deleteById(id);
    return true;
  }
}