package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.TyreDTO;

import java.util.List;

public interface TyreService {

    List<TyreDTO> getAll();

    /** @throws com.dolsk.tyres.exception.ResourceNotFoundException if not found */
    TyreDTO getById(Long id);

    TyreDTO create(TyreDTO dto);

    /** @throws com.dolsk.tyres.exception.ResourceNotFoundException if not found */
    TyreDTO update(Long id, TyreDTO dto);

    /**
     * @throws com.dolsk.tyres.exception.ResourceNotFoundException if not found
     * @throws com.dolsk.tyres.exception.ConflictException if referenced by orders
     */
    void delete(Long id);
}
