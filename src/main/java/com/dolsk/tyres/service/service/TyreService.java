package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.TyreDTO;
import java.util.List;

public interface TyreService {
  List<TyreDTO> getAll();
  TyreDTO getById(Long id);
  TyreDTO create(TyreDTO dto);
  TyreDTO update(Long id, TyreDTO dto);
  boolean delete(Long id);
}