package com.ahorraapp.service.impl;

import com.ahorraapp.dto.gasto.*;
import com.ahorraapp.model.Gasto;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.GastoRepository;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.GastoService;
import com.ahorraapp.service.LogroService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogroService logroService;

    @Override
    public GastoDTO crearGasto(GastoCrearDTO dto) {

        Usuario usuario = obtenerUsuarioAutenticado();

        Gasto gasto = new Gasto();
        gasto.setUsuario(usuario);
        gasto.setCategoria(dto.getCategoria());
        gasto.setMonto(dto.getMonto() != null ? dto.getMonto() : BigDecimal.ZERO);
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setTipoRegistro(dto.getTipoRegistro());
        gasto.setArchivoUrl(dto.getArchivoUrl());
        gasto.setFechaRegistro(LocalDateTime.now());

        Gasto saved = gastoRepository.save(gasto);

        logroService.procesarLogrosPorGasto(usuario, saved);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> listarGastosUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());
        return gastos.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> listarGastosPorDia(LocalDate dia) {
        Usuario usuario = obtenerUsuarioAutenticado();
        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());
        return gastos.stream()
                .filter(g -> g.getFechaRegistro() != null && g.getFechaRegistro().toLocalDate().isEqual(dia))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> listarGastosPorSemana(LocalDate fechaEnLaSemana) {
        Usuario usuario = obtenerUsuarioAutenticado();
        LocalDate inicioSemana = fechaEnLaSemana.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = inicioSemana.plusDays(6);

        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());
        return gastos.stream()
                .filter(g -> {
                    if (g.getFechaRegistro() == null)
                        return false;
                    LocalDate fecha = g.getFechaRegistro().toLocalDate();
                    return (!fecha.isBefore(inicioSemana)) && (!fecha.isAfter(finSemana));
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> listarGastosPorMes(int mes, int anio) {
        Usuario usuario = obtenerUsuarioAutenticado();
        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());
        return gastos.stream()
                .filter(g -> {
                    if (g.getFechaRegistro() == null)
                        return false;
                    LocalDate fecha = g.getFechaRegistro().toLocalDate();
                    return fecha.getMonthValue() == mes && fecha.getYear() == anio;
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GastoDTO actualizarGasto(Long idGasto, GastoCrearDTO dto) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Gasto gasto = gastoRepository.findById(idGasto)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado."));

        if (!gasto.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tiene permiso para modificar este gasto.");
        }

        // actualizar campos permitidos
        gasto.setCategoria(dto.getCategoria());
        gasto.setMonto(dto.getMonto() != null ? dto.getMonto() : gasto.getMonto());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setTipoRegistro(dto.getTipoRegistro());
        gasto.setArchivoUrl(dto.getArchivoUrl());
        // no tocamos fechaRegistro salvo que quieras permitir editarla

        Gasto updated = gastoRepository.save(gasto);
        return mapToDto(updated);
    }

    @Override
    public void eliminarGasto(Long idGasto) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Gasto gasto = gastoRepository.findById(idGasto)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado."));

        if (!gasto.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tiene permiso para eliminar este gasto.");
        }

        gastoRepository.delete(gasto);
    }

    /* ---------------------- Helpers ---------------------- */

    private Usuario obtenerUsuarioAutenticado() {
        String correo = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado."));

        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    private GastoDTO mapToDto(Gasto g) {
        if (g == null)
            return null;
        return GastoDTO.builder()
                .idGasto(g.getIdGasto())
                .monto(g.getMonto())
                .categoria(g.getCategoria())
                .descripcion(g.getDescripcion())
                .tipoRegistro(g.getTipoRegistro())
                .archivoUrl(g.getArchivoUrl())
                .fechaRegistro(g.getFechaRegistro())
                .build();
    }
}
