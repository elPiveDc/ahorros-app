package com.ahorraapp.controller;

import com.ahorraapp.dto.logro.LogroDTO;
import com.ahorraapp.service.LogroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
public class LogroController {

    private final LogroService logroService;

    @GetMapping
    public ResponseEntity<List<LogroDTO>> listarLogros() {
        return ResponseEntity.ok(logroService.listarLogrosUsuario());
    }

    // Solo si quieres permitir crear logros manuales
    @PostMapping("/manual")
    public ResponseEntity<LogroDTO> crearManual(
            @Valid @RequestBody LogroDTO dto) {

        return ResponseEntity.ok(logroService.crearLogroManual(dto));
    }
}
