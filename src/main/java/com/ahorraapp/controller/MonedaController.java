package com.ahorraapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ahorraapp.dto.moneda.TransaccionMonedaDTO;
import com.ahorraapp.service.MonedaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/monedas")
@RequiredArgsConstructor
public class MonedaController {

    private final MonedaService monedaService;

    @GetMapping("/balance")
    public Integer balance() {
        return monedaService.obtenerBalance();
    }

    @GetMapping("/historial")
    public List<TransaccionMonedaDTO> historial() {
        return monedaService.historial();
    }

    @GetMapping("/historial/ganancias")
    public List<TransaccionMonedaDTO> ganancias() {
        return monedaService.historialGanancias();
    }

    @GetMapping("/historial/gastos")
    public List<TransaccionMonedaDTO> gastos() {
        return monedaService.historialGastos();
    }

    @PostMapping("/ganar")
    public TransaccionMonedaDTO ganar(@RequestParam Integer cantidad,
            @RequestParam String motivo) {
        return monedaService.registrarGanancia(cantidad, motivo);
    }

    @PostMapping("/gastar")
    public TransaccionMonedaDTO gastar(@RequestParam Integer cantidad,
            @RequestParam String motivo) {
        return monedaService.registrarGasto(cantidad, motivo);
    }
}
