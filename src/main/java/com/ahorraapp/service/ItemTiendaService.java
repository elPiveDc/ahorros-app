package com.ahorraapp.service;

import java.util.List;

import com.ahorraapp.dto.tienda.*;

public interface ItemTiendaService {

    List<ItemTiendaDTO> listarItems();

    ItemTiendaDTO crearItem(ItemTiendaCrearDTO dto); // Administrador
}