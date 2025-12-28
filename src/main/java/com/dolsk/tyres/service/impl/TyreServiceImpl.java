package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.TyreDTO;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.repository.OrderRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.service.service.TyreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TyreServiceImpl implements TyreService {

  private final TyreRepository tyreRepo;
  private final OrderRepository orderRepo;

  // =========================
  // ENTITY → DTO
  // =========================
  private TyreDTO map(Tyre t) {
    return new TyreDTO(
            t.getId(),
            t.getBrand(),
            t.getSize(),
            t.getPrice(),
            t.getDescription(),
            t.getImageUrl()
    );
  }

  // =========================
  // READ
  // =========================
  @Override
  public List<TyreDTO> getAll() {
    return tyreRepo.findAll()
            .stream()
            .map(this::map)
            .collect(Collectors.toList());
  }

  @Override
  public TyreDTO getById(Long id) {
    Tyre t = tyreRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tyre not found"));
    return map(t);
  }

  // =========================
  // CREATE
  // =========================
  @Override
  public TyreDTO create(TyreDTO dto) {
    Tyre tyre = new Tyre();

    tyre.setBrand(dto.getBrand());
    tyre.setSize(dto.getSize());
    tyre.setPrice(dto.getPrice());
    tyre.setDescription(dto.getDescription());
    tyre.setImageUrl(dto.getImageUrl());

    return map(tyreRepo.save(tyre));
  }

  // =========================
  // UPDATE
  // =========================
  @Override
  public TyreDTO update(Long id, TyreDTO dto) {
    Tyre tyre = tyreRepo.findById(id)
            .orElseThrow(() ->
                    new IllegalStateException("Tyre not found with id " + id)
            );

    tyre.setBrand(dto.getBrand());
    tyre.setSize(dto.getSize());
    tyre.setPrice(dto.getPrice());
    tyre.setDescription(dto.getDescription());
    tyre.setImageUrl(dto.getImageUrl());

    return map(tyreRepo.save(tyre));
  }

  // =========================
  // DELETE
  // =========================
  @Override
  public boolean delete(Long id) {
    if (!tyreRepo.existsById(id)) {
      return false;
    }

    if (orderRepo.existsByTyreId(id)) {
      throw new IllegalStateException(
              "Cannot delete tyre: it is referenced by existing orders"
      );
    }

    tyreRepo.deleteById(id);
    return true;
  }
}
