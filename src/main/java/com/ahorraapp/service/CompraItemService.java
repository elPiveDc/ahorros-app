package com.ahorraapp.service;

import java.util.List;

import com.ahorraapp.dto.tienda.ItemTiendaDTO;

public interface CompraItemService {

    List<ItemTiendaDTO> listarMisCompras();

    String comprarItem(Long idItem);
}
