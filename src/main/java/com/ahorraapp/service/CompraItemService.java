package com.ahorraapp.service;

import java.util.List;

import com.ahorraapp.dto.tienda.CompraItemDTO;

public interface CompraItemService {

    List<CompraItemDTO> listarMisCompras();

    CompraItemDTO comprarItem(Long idItem);
}
