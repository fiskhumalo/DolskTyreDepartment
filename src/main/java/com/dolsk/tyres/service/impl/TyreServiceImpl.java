package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.TyreDTO;
import com.dolsk.tyres.exception.ConflictException;
import com.dolsk.tyres.exception.ResourceNotFoundException;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.repository.OrderRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.service.service.TyreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TyreServiceImpl implements TyreService {

    private final TyreRepository tyreRepository;
    private final OrderRepository orderRepository;

    // ── Mapping ───────────────────────────────────────────────────────────────
    // Lives in the service. TyreDTO has NO knowledge of the Tyre entity.

    private TyreDTO toDto(Tyre tyre) {
        return new TyreDTO(
                tyre.getId(),
                tyre.getBrand(),
                tyre.getSize(),
                tyre.getPrice(),
                tyre.getDescription(),
                tyre.getImageUrl()
        );
    }

    private Tyre toEntity(TyreDTO dto) {
        Tyre tyre = new Tyre();
        tyre.setBrand(dto.getBrand());
        tyre.setSize(dto.getSize());
        tyre.setPrice(dto.getPrice());
        tyre.setDescription(dto.getDescription());
        tyre.setImageUrl(dto.getImageUrl());
        return tyre;
    }

    // ── Operations ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TyreDTO> getAll() {
        return tyreRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TyreDTO getById(Long id) {
        return tyreRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tyre not found with id: " + id));
    }

    @Override
    @Transactional
    public TyreDTO create(TyreDTO dto) {
        return toDto(tyreRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public TyreDTO update(Long id, TyreDTO dto) {
        Tyre tyre = tyreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tyre not found with id: " + id));

        tyre.setBrand(dto.getBrand());
        tyre.setSize(dto.getSize());
        tyre.setPrice(dto.getPrice());
        tyre.setDescription(dto.getDescription());
        tyre.setImageUrl(dto.getImageUrl());

        // No explicit save() needed — dirty checking commits changes on transaction end
        return toDto(tyre);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tyreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tyre not found with id: " + id);
        }
        if (orderRepository.existsByTyreId(id)) {
            throw new ConflictException(
                    "Cannot delete tyre with id " + id
                            + ": it is referenced by existing orders");
        }
        tyreRepository.deleteById(id);
    }
}
