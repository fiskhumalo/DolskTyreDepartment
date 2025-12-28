package com.dolsk.tyres.repository;

import com.dolsk.tyres.model.Review;
import com.dolsk.tyres.model.Tyre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTyre(Tyre tyre);
}
