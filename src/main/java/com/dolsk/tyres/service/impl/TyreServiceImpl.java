package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.PagedResponse;
import com.dolsk.tyres.dto.TyreDTO;
import com.dolsk.tyres.exception.ConflictException;
import com.dolsk.tyres.exception.ResourceNotFoundException;
import com.dolsk.tyres.model.Tyre;
import com.dolsk.tyres.repository.OrderRepository;
import com.dolsk.tyres.repository.TyreRepository;
import com.dolsk.tyres.service.service.TyreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TyreServiceImpl implements TyreService {

    private static final Logger logger = LoggerFactory.getLogger(TyreServiceImpl.class);

    private final TyreRepository tyreRepository;
    private final OrderRepository orderRepository;

    // ── Mapping ───────────────────────────────────────────────────────────────

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
    public PagedResponse<TyreDTO> getAllPaged(int page, int size, String sortBy, String direction, String brand) {
        String safeSortBy = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "price" -> "price";
            case "brand" -> "brand";
            case "size" -> "size";
            default -> "id";
        };

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(safeSortBy).descending()
                : Sort.by(safeSortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Tyre> result = tyreRepository.findAllFiltered(brand, pageable);

        return PagedResponse.from(result, this::toDto);
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
        Tyre saved = tyreRepository.save(toEntity(dto));
        logger.info("[AUDIT] action=CREATE_TYRE tyreId={} brand={} size={} price={}",
                saved.getId(), saved.getBrand(), saved.getSize(), saved.getPrice());
        return toDto(saved);
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

        logger.info("[AUDIT] action=UPDATE_TYRE tyreId={} brand={} price={}",
                id, dto.getBrand(), dto.getPrice());
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
        logger.info("[AUDIT] action=DELETE_TYRE tyreId={}", id);
    }
}
