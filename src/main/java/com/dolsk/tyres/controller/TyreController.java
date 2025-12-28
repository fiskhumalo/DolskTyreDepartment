package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.TyreDTO;
import com.dolsk.tyres.service.service.TyreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tyres")
@RequiredArgsConstructor
public class TyreController {
  private final TyreService tyreService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TyreDTO>>> list() {
    List<TyreDTO> dtos = tyreService.getAll();
    return ResponseEntity.ok(new ApiResponse<>(true, dtos, null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TyreDTO>> get(@PathVariable Long id) {
    TyreDTO dto = tyreService.getById(id);
    return ResponseEntity.ok(new ApiResponse<>(true, dto, null));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TyreDTO>> create(@Validated @RequestBody TyreDTO dto) {
    TyreDTO created = tyreService.create(dto);
    return ResponseEntity.ok(new ApiResponse<>(true, created, null));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TyreDTO>> update(
          @PathVariable Long id,
          @Validated @RequestBody TyreDTO dto) {

    try {
      TyreDTO updatedTyre = tyreService.update(id, dto);
      return ResponseEntity.ok(
              new ApiResponse<>(true, updatedTyre, "Tyre with id " + id + " has been successfully updated")
      );
    } catch (IllegalStateException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ApiResponse<>(false, null, ex.getMessage()));
    }
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTyre(@PathVariable Long id) {
    try {
      boolean deleted = tyreService.delete(id);
      if (!deleted) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, null, "Tyre not found"));
      }
      return ResponseEntity.ok(new ApiResponse<>(true, null,  "Tyre with id " + id + " has been successfully deleted"));
    } catch (IllegalStateException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new ApiResponse<>(false, null, ex.getMessage()));
    }
  }}