package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
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
 * No try/catch — exceptions propagate to GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/tyres")
@RequiredArgsConstructor
public class TyreController {

    private final TyreService tyreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TyreDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(tyreService.getAll()));
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
