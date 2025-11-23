package com.ahorraapp.controller;

import com.ahorraapp.dto.gasto.GastoCrearDTO;
import com.ahorraapp.dto.gasto.GastoDTO;
import com.ahorraapp.service.GastoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @PostMapping
    public ResponseEntity<GastoDTO> crearGasto(@Valid @RequestBody GastoCrearDTO dto) {
        return ResponseEntity.ok(gastoService.crearGasto(dto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<GastoDTO>> listarGastosUsuario() {
        return ResponseEntity.ok(gastoService.listarGastosUsuario());
    }

    @GetMapping("/dia/{fecha}")
    public ResponseEntity<?> listarGastosPorDia(@PathVariable String fecha) {
        try {
            LocalDate dia = LocalDate.parse(fecha);
            return ResponseEntity.ok(gastoService.listarGastosPorDia(dia));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use YYYY-MM-DD");
        }
    }

    @GetMapping("/semana/{fecha}")
    public ResponseEntity<?> listarGastosPorSemana(@PathVariable String fecha) {
        try {
            LocalDate f = LocalDate.parse(fecha);
            return ResponseEntity.ok(gastoService.listarGastosPorSemana(f));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use YYYY-MM-DD");
        }
    }

    @GetMapping("/mes")
    public ResponseEntity<List<GastoDTO>> listarGastosPorMes(
            @RequestParam int mes,
            @RequestParam int anio) {

        return ResponseEntity.ok(gastoService.listarGastosPorMes(mes, anio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDTO> actualizarGasto(
            @PathVariable Long id,
            @Valid @RequestBody GastoCrearDTO dto) {

        return ResponseEntity.ok(gastoService.actualizarGasto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
        return ResponseEntity.noContent().build();
    }
}
