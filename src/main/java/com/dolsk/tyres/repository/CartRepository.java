package com.dolsk.tyres.repository;

import com.dolsk.tyres.model.Cart;
import com.dolsk.tyres.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Fetches the cart with items AND each item's tyre in a single query.
     * Eliminates the N+1 problem: without this, Hibernate would fire
     * 1 query for the cart + 1 per item + 1 per tyre = O(n) queries.
     * With JOIN FETCH: always exactly 1 query regardless of item count.
     */
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.items i " +
           "LEFT JOIN FETCH i.tyre " +
           "WHERE c.user = :user")
    Optional<Cart> findByUserWithItems(@Param("user") User user);

    Optional<Cart> findByUser(User user);
}
