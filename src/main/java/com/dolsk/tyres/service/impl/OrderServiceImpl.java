package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.OrderDTO;
import com.dolsk.tyres.dto.PagedResponse;
import com.dolsk.tyres.exception.ResourceNotFoundException;
import com.dolsk.tyres.model.Order;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.OrderRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final TyreRepository tyreRepository;
    private final UserRepository userRepository;

    // ── Mapping ───────────────────────────────────────────────────────────────

    private OrderDTO toDto(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getTyre().getId(),
                order.getQuantity(),
                order.getUser().getUsername()
        );
    }

    // ── Operations ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        Tyre tyre = tyreRepository.findById(dto.getTyreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tyre not found with id: " + dto.getTyreId()));

        Order order = new Order(null, user, tyre, dto.getQuantity(), LocalDateTime.now());
        Order saved = orderRepository.save(order);

        logger.info("[AUDIT] action=PLACE_ORDER user={} orderId={} tyreId={} quantity={}",
                username, saved.getId(), dto.getTyreId(), dto.getQuantity());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));
        return orderRepository.findAllByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDTO> getAllOrdersPaged(int page, int size) {
        Page<Order> result = orderRepository.findAllWithDetails(
                PageRequest.of(page, size, Sort.by("orderDate").descending()));

        return PagedResponse.from(result, this::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
        logger.info("[AUDIT] action=DELETE_ORDER orderId={}", id);
    }
}
