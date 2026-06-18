package com.dolsk.tyres.repository;

import com.dolsk.tyres.model.Order;
import com.dolsk.tyres.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser(User user);

    boolean existsByTyreId(Long tyreId);

    /**
     * Paginated all-orders query with JOIN FETCH to avoid N+1 on user/tyre.
     * Uses countQuery separately because JOIN FETCH + pagination requires it.
     */
    @Query(value = "SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.tyre",
           countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithDetails(Pageable pageable);

    /**
     * Paginated orders for a specific user with tyre eagerly loaded.
     */
    @Query(value = "SELECT o FROM Order o JOIN FETCH o.tyre WHERE o.user = :user",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Page<Order> findByUserWithDetails(@Param("user") User user, Pageable pageable);
}
