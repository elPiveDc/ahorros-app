package com.ahorraapp.service;

import com.ahorraapp.dto.gasto.*;
import com.ahorraapp.model.Gasto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

public interface GastoService {

    GastoDTO crearGasto(GastoCrearDTO dto);

    List<GastoDTO> listarGastosUsuario();

    List<GastoDTO> listarGastosPorDia(LocalDate dia);

    /**
     * Lista gastos en la semana que contiene la fecha dada.
     * La semana va de lunes (inclusive) a domingo (inclusive).
     */
    List<GastoDTO> listarGastosPorSemana(LocalDate fechaEnLaSemana);

    /**
     * Lista gastos por mes (mes: 1..12) y año (ej. 2025).
     */
    List<GastoDTO> listarGastosPorMes(int mes, int anio);

    GastoDTO actualizarGasto(Long idGasto, GastoCrearDTO dto);

    void eliminarGasto(Long idGasto);
}
