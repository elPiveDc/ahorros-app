package com.ahorraapp.service;

import java.util.List;

import com.ahorraapp.dto.tienda.CosmeticoDTO;

public interface CosmeticoService {

    List<CosmeticoDTO> listarCosmeticos();

    String aplicarCosmetico(Long idCosmetico);

    List<CosmeticoDTO> misCosmeticos();
}
