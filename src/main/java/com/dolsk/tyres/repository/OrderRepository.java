package com.dolsk.tyres.repository;


import com.dolsk.tyres.model.Order;
import com.dolsk.tyres.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
    List<Order> findAll();

    boolean existsByTyreId(Long id);
}
