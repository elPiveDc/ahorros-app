package com.ahorraapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahorraapp.dto.tienda.ItemTiendaCrearDTO;
import com.ahorraapp.dto.tienda.ItemTiendaDTO;
import com.ahorraapp.service.ItemTiendaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tienda")
@RequiredArgsConstructor
public class ItemTiendaController {

    private final ItemTiendaService itemTiendaService;

    @GetMapping
    public List<ItemTiendaDTO> listar() {
        return itemTiendaService.listarItems();
    }

    @PostMapping
    public ItemTiendaDTO crear(@RequestBody @Valid ItemTiendaCrearDTO dto) {
        return itemTiendaService.crearItem(dto);
    }
}
