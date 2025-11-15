package com.ahorraapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahorraapp.dto.tienda.*;
import com.ahorraapp.model.ItemTienda;
import com.ahorraapp.repository.ItemTiendaRepository;
import com.ahorraapp.service.ItemTiendaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemTiendaServiceImpl implements ItemTiendaService {

    private final ItemTiendaRepository itemRepo;

    @Override
    public List<ItemTiendaDTO> listarItems() {
        return itemRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ItemTiendaDTO crearItem(ItemTiendaCrearDTO dto) {

        ItemTienda item = ItemTienda.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .costo(dto.getCosto())
                .tipo(dto.getTipo())
                .imagenUrl(dto.getImagenUrl())
                .build();

        return mapToDto(itemRepo.save(item));
    }

    private ItemTiendaDTO mapToDto(ItemTienda item) {
        return ItemTiendaDTO.builder()
                .idItem(item.getIdItem())
                .nombre(item.getNombre())
                .descripcion(item.getDescripcion())
                .costo(item.getCosto())
                .tipo(item.getTipo())
                .imagenUrl(item.getImagenUrl())
                .build();
    }
}
