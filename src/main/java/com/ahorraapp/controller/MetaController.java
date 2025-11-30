package com.ahorraapp.controller;

import com.ahorraapp.dto.meta.MetaCrearDTO;
import com.ahorraapp.dto.meta.MetaDTO;
import com.ahorraapp.service.MetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @PostMapping
    public ResponseEntity<MetaDTO> crearMeta(
            @Valid @RequestBody MetaCrearDTO dto) {

        return ResponseEntity.ok(metaService.crearMeta(dto));
    }

    @GetMapping
    public ResponseEntity<List<MetaDTO>> listarMetas() {
        return ResponseEntity.ok(metaService.listarMetasUsuario());
    }

    @GetMapping("/{idMeta}")
    public ResponseEntity<MetaDTO> obtener(@PathVariable Long idMeta) {
        return ResponseEntity.ok(metaService.obtenerMeta(idMeta));
    }

    @PutMapping("/{idMeta}")
    public ResponseEntity<MetaDTO> actualizar(
            @PathVariable Long idMeta,
            @Valid @RequestBody MetaCrearDTO dto) {

        return ResponseEntity.ok(metaService.actualizarMeta(idMeta, dto));
    }

    @DeleteMapping("/{idMeta}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idMeta) {
        metaService.eliminarMeta(idMeta);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idMeta}/cumplir")
    public ResponseEntity<MetaDTO> marcarComoCumplida(@PathVariable Long idMeta) {
        return ResponseEntity.ok(metaService.marcarComoCumplida(idMeta));
    }
}
