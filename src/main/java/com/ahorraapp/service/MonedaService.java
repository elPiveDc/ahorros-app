package com.ahorraapp.service;

import java.util.List;

import com.ahorraapp.dto.moneda.TransaccionMonedaDTO;

public interface MonedaService {

    Integer obtenerBalance();

    List<TransaccionMonedaDTO> historial();

    List<TransaccionMonedaDTO> historialGanancias();

    List<TransaccionMonedaDTO> historialGastos();

    TransaccionMonedaDTO registrarGanancia(Integer cantidad, String motivo);

    TransaccionMonedaDTO registrarGasto(Integer cantidad, String motivo);
}
