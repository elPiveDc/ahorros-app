package com.ahorraapp.service.impl;

import com.ahorraapp.dto.logro.LogroDTO;
import com.ahorraapp.model.Gasto;
import com.ahorraapp.model.Logro;
import com.ahorraapp.model.Meta;
import com.ahorraapp.model.TransaccionMoneda;
import com.ahorraapp.model.Usuario;

import com.ahorraapp.repository.GastoRepository;
import com.ahorraapp.repository.LogroRepository;
import com.ahorraapp.repository.MetaRepository;
import com.ahorraapp.repository.TransaccionMonedaRepository;
import com.ahorraapp.repository.UsuarioRepository;

import com.ahorraapp.service.LogroService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LogroServiceImpl implements LogroService {

    private final LogroRepository logroRepository;
    private final UsuarioRepository usuarioRepository;
    private final GastoRepository gastoRepository;
    private final MetaRepository metaRepository;
    private final TransaccionMonedaRepository transaccionMonedaRepository;

    private final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /*
     * ------------------------------------------------------------
     * API PÚBLICA
     * ------------------------------------------------------------
     */

    @Override
    @Transactional(readOnly = true)
    public List<LogroDTO> listarLogrosUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return logroRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LogroDTO crearLogroManual(LogroDTO dto) {
        Usuario usuario = obtenerUsuarioAutenticado();

        if (logroYaObtenido(usuario, dto.getNombre()))
            throw new RuntimeException("Logro ya obtenido");

        Logro logro = Logro.builder()
                .usuario(usuario)
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .puntos(dto.getPuntos())
                .fechaLogro(java.time.LocalDateTime.now())
                .build();

        Logro saved = logroRepository.save(logro);

        if (saved.getPuntos() != null && saved.getPuntos() > 0) {
            otorgarMonedas(usuario, saved.getPuntos(), "Logro: " + saved.getNombre());
        }

        return mapToDto(saved);
    }

    @Override
    public void procesarLogrosPorGasto(Usuario usuario, Gasto gasto) {

        // 1) Primer gasto
        if (!logroYaObtenido(usuario, "Primer gasto registrado")) {
            long cant = gastoRepository.countByUsuarioIdUsuario(usuario.getIdUsuario());
            if (cant == 1) {
                otorgarLogroYMonedas(usuario, "Primer gasto registrado",
                        "Has registrado tu primer gasto.", 5);
            }
        }

        // 2) Primer gasto multimedia
        if (("foto".equalsIgnoreCase(gasto.getTipoRegistro()) ||
                "voz".equalsIgnoreCase(gasto.getTipoRegistro()))
                && !logroYaObtenido(usuario, "Primer gasto multimedia")) {

            long prev = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario())
                    .stream()
                    .filter(g -> !"texto".equalsIgnoreCase(g.getTipoRegistro()))
                    .count();

            if (prev == 1) {
                otorgarLogroYMonedas(usuario, "Primer gasto multimedia",
                        "Subiste tu primer gasto con foto/voz.", 5);
            }
        }

        // 3) Rachas
        int racha = calcularRachaDias(usuario);
        if (racha >= 3 && !logroYaObtenido(usuario, "Racha 3 dias")) {
            otorgarLogroYMonedas(usuario, "Racha 3 dias",
                    "3 días seguidos registrando gastos.", 10);
        }
        if (racha >= 7 && !logroYaObtenido(usuario, "Racha 7 dias")) {
            otorgarLogroYMonedas(usuario, "Racha 7 dias",
                    "7 días seguidos registrando gastos.", 20);
        }

        // 4) 5 gastos multimedia
        long multi = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario())
                .stream()
                .filter(g -> !"texto".equalsIgnoreCase(g.getTipoRegistro()))
                .count();

        if (multi >= 5 && !logroYaObtenido(usuario, "5 gastos multimedia")) {
            otorgarLogroYMonedas(usuario, "5 gastos multimedia",
                    "Has registrado 5 gastos multimedia.", 15);
        }
    }

    @Override
    public void procesarLogrosPorMeta(Usuario usuario, Meta meta) {

        long metasCompletadas = metaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getCumplida()))
                .count();

        if (metasCompletadas >= 1 && !logroYaObtenido(usuario, "Primera meta completada")) {
            otorgarLogroYMonedas(usuario, "Primera meta completada",
                    "Completaste tu primera meta.", 20);
        }

        if (metasCompletadas >= 3 && !logroYaObtenido(usuario, "3 metas completadas")) {
            otorgarLogroYMonedas(usuario, "3 metas completadas",
                    "Completaste 3 metas.", 30);
        }

        // XP total acumulado
        java.math.BigDecimal xp = metaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(m -> m.getMontoActual() != null ? m.getMontoActual() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        if (xp.compareTo(java.math.BigDecimal.valueOf(100)) >= 0 &&
                !logroYaObtenido(usuario, "100 XP acumulado")) {

            otorgarLogroYMonedas(usuario, "100 XP acumulado",
                    "Has acumulado 100 XP en metas.", 15);
        }
    }

    @Override
    public boolean logroYaObtenido(Usuario usuario, String nombreLogro) {
        return logroRepository.existsByUsuarioIdUsuarioAndNombreIgnoreCase(
                usuario.getIdUsuario(),
                nombreLogro);
    }

    /*
     * ------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------
     */

    private Usuario obtenerUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    private LogroDTO mapToDto(Logro l) {
        return LogroDTO.builder()
                .idLogro(l.getIdLogro())
                .nombre(l.getNombre())
                .descripcion(l.getDescripcion())
                .puntos(l.getPuntos())
                .fechaLogro(l.getFechaLogro() != null ? l.getFechaLogro().format(ISO) : null)
                .build();
    }

    private void otorgarLogroYMonedas(Usuario usuario, String nombre, String descripcion, int puntos) {

        if (logroYaObtenido(usuario, nombre))
            return;

        Logro logro = Logro.builder()
                .usuario(usuario)
                .nombre(nombre)
                .descripcion(descripcion)
                .puntos(puntos)
                .fechaLogro(java.time.LocalDateTime.now())
                .build();

        logroRepository.save(logro);

        otorgarMonedas(usuario, puntos, "Logro: " + nombre);
    }

    private void otorgarMonedas(Usuario usuario, int cantidad, String motivo) {
        usuario.setMonedas((usuario.getMonedas() != null ? usuario.getMonedas() : 0) + cantidad);
        usuarioRepository.save(usuario);

        TransaccionMoneda t = TransaccionMoneda.builder()
                .usuario(usuario)
                .tipo("ganancia")
                .motivo(motivo)
                .cantidad(cantidad)
                .fecha(java.time.LocalDateTime.now())
                .build();

        transaccionMonedaRepository.save(t);
    }

    private int calcularRachaDias(Usuario usuario) {
        List<Gasto> gastos = gastoRepository.findByUsuarioIdUsuarioOrderByFechaRegistroDesc(usuario.getIdUsuario());

        Set<java.time.LocalDate> dias = gastos.stream()
                .map(g -> g.getFechaRegistro().toLocalDate())
                .collect(Collectors.toSet());

        int racha = 0;
        java.time.LocalDate hoy = java.time.LocalDate.now();

        while (dias.contains(hoy.minusDays(racha))) {
            racha++;
        }

        return racha;
    }
}
