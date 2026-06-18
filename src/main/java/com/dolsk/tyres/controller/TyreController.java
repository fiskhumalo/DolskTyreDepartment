package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.PagedResponse;
import com.dolsk.tyres.dto.TyreDTO;
import com.dolsk.tyres.service.service.TyreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tyre catalogue endpoints.
 *
 * Read operations: any authenticated user.
 * Write operations: ROLE_ADMIN only (enforced via @PreAuthorize).
 *
 * Supports both:
 *   GET /api/tyres         → returns ALL tyres (backward compatible)
 *   GET /api/tyres/search  → paginated + sorted + filtered
 */
@RestController
@RequestMapping("/api/tyres")
@RequiredArgsConstructor
public class TyreController {

    private final TyreService tyreService;

    /**
     * GET /api/tyres
     * Returns all tyres without pagination (backward compatible with frontend).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TyreDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(tyreService.getAll()));
    }

    /**
     * GET /api/tyres/search?page=0&size=10&sort=price&direction=asc&brand=Michelin
     * Paginated, sorted, and filterable tyre listing.
     *
     * @param page      Page number (0-based, default 0)
     * @param size      Items per page (default 10, max 50)
     * @param sort      Sort field: price, brand, size (default: id)
     * @param direction Sort direction: asc or desc (default: asc)
     * @param brand     Optional brand filter (partial match, case-insensitive)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<TyreDTO>>> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String brand) {
        // Cap page size at 50 to prevent memory abuse
        int safeSize = Math.min(size, 50);
        PagedResponse<TyreDTO> result = tyreService.getAllPaged(page, safeSize, sort, direction, brand);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TyreDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tyreService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TyreDTO>> create(@Valid @RequestBody TyreDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(tyreService.create(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TyreDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody TyreDTO dto) {
        TyreDTO updated = tyreService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.ok(updated, "Tyre with id " + id + " has been successfully updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        tyreService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Tyre with id " + id + " has been successfully deleted"));
    }
}
