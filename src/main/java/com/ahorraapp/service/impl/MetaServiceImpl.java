package com.ahorraapp.service.impl;

import com.ahorraapp.dto.meta.MetaCrearDTO;
import com.ahorraapp.dto.meta.MetaDTO;
import com.ahorraapp.model.Gasto;
import com.ahorraapp.model.Meta;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.GastoRepository;
import com.ahorraapp.repository.MetaRepository;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.MetaService;
import com.ahorraapp.service.LogroService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MetaServiceImpl implements MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GastoRepository gastoRepository;
    private final LogroService logroService; // Para logros al completar metas

    @Override
    public MetaDTO crearMeta(MetaCrearDTO dto) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Meta meta = new Meta();
        meta.setUsuario(usuario);
        meta.setTitulo(dto.getTitulo());
        meta.setDescripcion(dto.getDescripcion());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setMontoActual(BigDecimal.ZERO); // 🔥 BigDecimal correcto
        meta.setFechaInicio(dto.getFechaInicio());
        meta.setFechaFin(dto.getFechaFin());
        meta.setCumplida(false);

        return mapToDTO(metaRepository.save(meta));
    }

    @Override
    public List<MetaDTO> listarMetasUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return metaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MetaDTO obtenerMeta(Long idMeta) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        validarPropietario(meta.getUsuario());
        return mapToDTO(meta);
    }

    @Override
    public MetaDTO actualizarMeta(Long idMeta, MetaCrearDTO dto) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        validarPropietario(meta.getUsuario());

        meta.setTitulo(dto.getTitulo());
        meta.setDescripcion(dto.getDescripcion());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setFechaInicio(dto.getFechaInicio());
        meta.setFechaFin(dto.getFechaFin());

        return mapToDTO(metaRepository.save(meta));
    }

    @Override
    public void eliminarMeta(Long idMeta) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        validarPropietario(meta.getUsuario());
        metaRepository.delete(meta);
    }

    /**
     * ===========================================================
     * PROGRESO AUTOMÁTICO (XP) POR CADA GASTO
     * ===========================================================
     */
    @Override
    public MetaDTO procesarProgresoPorGasto(Long idGasto) {

        Gasto gasto = gastoRepository.findById(idGasto)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        Usuario usuario = gasto.getUsuario();

        List<Meta> metasActivas = metaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .filter(m -> !Boolean.TRUE.equals(m.getCumplida()))
                .collect(Collectors.toList());

        if (metasActivas.isEmpty())
            return null;

        Meta meta = metasActivas.get(0); // Por ahora solo una meta activa

        double xp = 1;

        if ("foto".equalsIgnoreCase(gasto.getTipoRegistro()) ||
                "voz".equalsIgnoreCase(gasto.getTipoRegistro())) {
            xp += 2;
        }

        List<String> buenas = Arrays.asList("hogar", "comida casera", "transporte", "mercado");
        if (buenas.contains(gasto.getCategoria().toLowerCase())) {
            xp += 2;
        }

        xp += calcularBonusRacha(usuario);

        meta.setMontoActual(meta.getMontoActual().add(BigDecimal.valueOf(xp)));

        // ¿Se cumplió la meta?
        if (meta.getMontoActual().compareTo(meta.getMontoObjetivo()) >= 0) {
            meta.setCumplida(true);

            // LOGROS POR META COMPLETADA
            logroService.procesarLogrosPorMeta(usuario, meta);
        }

        return mapToDTO(metaRepository.save(meta));
    }

    private double calcularBonusRacha(Usuario usuario) {
        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());

        Set<LocalDate> dias = gastos.stream()
                .map(g -> g.getFechaRegistro().toLocalDate())
                .collect(Collectors.toSet());

        int racha = 0;
        LocalDate hoy = LocalDate.now();

        while (dias.contains(hoy.minusDays(racha))) {
            racha++;
        }

        if (racha >= 7)
            return 20;
        if (racha >= 5)
            return 10;
        if (racha >= 2)
            return 3;

        return 0;
    }

    @Override
    public MetaDTO marcarComoCumplida(Long idMeta) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        validarPropietario(meta.getUsuario());
        meta.setCumplida(true);

        // 🔥 Logros manuales también
        logroService.procesarLogrosPorMeta(meta.getUsuario(), meta);

        return mapToDTO(metaRepository.save(meta));
    }

    /* ---------------------------------------------------------------------- */

    private Usuario obtenerUsuarioAutenticado() {
        String correo = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(a -> a.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validarPropietario(Usuario u) {
        if (!u.getCorreo().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("No autorizado.");
        }
    }

    private MetaDTO mapToDTO(Meta m) {
        return MetaDTO.builder()
                .idMeta(m.getIdMeta())
                .titulo(m.getTitulo())
                .descripcion(m.getDescripcion())
                .montoActual(m.getMontoActual())
                .montoObjetivo(m.getMontoObjetivo())
                .fechaInicio(m.getFechaInicio())
                .fechaFin(m.getFechaFin())
                .cumplida(Boolean.TRUE.equals(m.getCumplida()))
                .build();
    }
}
