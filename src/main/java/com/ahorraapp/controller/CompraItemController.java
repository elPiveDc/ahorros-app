package com.ahorraapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahorraapp.dto.tienda.CompraItemDTO;
import com.ahorraapp.service.CompraItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tienda/compras")
@RequiredArgsConstructor
public class CompraItemController {

    private final CompraItemService compraItemService;

    @GetMapping
    public List<CompraItemDTO> misCompras() {
        return compraItemService.listarMisCompras();
    }

    @PostMapping("/{idItem}")
    public CompraItemDTO comprar(@PathVariable Long idItem) {
        return compraItemService.comprarItem(idItem);
    }
}
