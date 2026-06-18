package com.dolsk.tyres.repository;

import com.dolsk.tyres.model.Tyre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TyreRepository extends JpaRepository<Tyre, Long> {

    /**
     * Paginated search with optional brand filter.
     * If brand is null or empty, returns all tyres paginated.
     */
    @Query("SELECT t FROM Tyre t WHERE " +
           "(:brand IS NULL OR :brand = '' OR LOWER(t.brand) LIKE LOWER(CONCAT('%', :brand, '%')))")
    Page<Tyre> findAllFiltered(@Param("brand") String brand, Pageable pageable);
}
