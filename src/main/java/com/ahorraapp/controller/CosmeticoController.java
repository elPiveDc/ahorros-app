package com.ahorraapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahorraapp.dto.tienda.CosmeticoDTO;
import com.ahorraapp.service.CosmeticoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cosmeticos")
@RequiredArgsConstructor
public class CosmeticoController {

    private final CosmeticoService cosmeticoService;

    @GetMapping
    public List<CosmeticoDTO> listar() {
        return cosmeticoService.listarCosmeticos();
    }

    @GetMapping("/mios")
    public List<CosmeticoDTO> misCosmeticos() {
        return cosmeticoService.misCosmeticos();
    }

    @PutMapping("/aplicar/{idCosmetico}")
    public String aplicar(@PathVariable Long idCosmetico) {
        return cosmeticoService.aplicarCosmetico(idCosmetico);
    }
}
