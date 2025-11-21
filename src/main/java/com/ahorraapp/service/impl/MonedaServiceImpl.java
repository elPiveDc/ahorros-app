package com.ahorraapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahorraapp.dto.moneda.TransaccionMonedaDTO;
import com.ahorraapp.model.TransaccionMoneda;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.TransaccionMonedaRepository;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.MonedaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MonedaServiceImpl implements MonedaService {

    private final UsuarioRepository usuarioRepo;
    private final TransaccionMonedaRepository transRepo;

    @Override
    public Integer obtenerBalance() {
        Usuario usuario = obtenerUsuario();
        return usuario.getMonedas();
    }

    @Override
    public List<TransaccionMonedaDTO> historial() {
        Usuario usuario = obtenerUsuario();

        return transRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransaccionMonedaDTO> historialGanancias() {
        Usuario usuario = obtenerUsuario();

        return transRepo.findByTipoAndUsuarioIdUsuario("ganancia", usuario.getIdUsuario())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransaccionMonedaDTO> historialGastos() {
        Usuario usuario = obtenerUsuario();

        return transRepo.findByTipoAndUsuarioIdUsuario("gasto", usuario.getIdUsuario())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public TransaccionMonedaDTO registrarGanancia(Integer cantidad, String motivo) {

        Usuario usuario = obtenerUsuario();
        usuario.setMonedas(usuario.getMonedas() + cantidad);
        usuarioRepo.save(usuario);

        TransaccionMoneda t = TransaccionMoneda.builder()
                .usuario(usuario)
                .tipo("ganancia")
                .motivo(motivo)
                .cantidad(cantidad)
                .fecha(LocalDateTime.now())
                .build();

        transRepo.save(t);

        return mapToDto(t);
    }

    @Override
    public TransaccionMonedaDTO registrarGasto(Integer cantidad, String motivo) {

        Usuario usuario = obtenerUsuario();

        if (usuario.getMonedas() < cantidad) {
            throw new RuntimeException("No tienes monedas suficientes");
        }

        usuario.setMonedas(usuario.getMonedas() - cantidad);
        usuarioRepo.save(usuario);

        TransaccionMoneda t = TransaccionMoneda.builder()
                .usuario(usuario)
                .tipo("gasto")
                .motivo(motivo)
                .cantidad(cantidad)
                .fecha(LocalDateTime.now())
                .build();

        transRepo.save(t);

        return mapToDto(t);
    }

    private Usuario obtenerUsuario() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private TransaccionMonedaDTO mapToDto(TransaccionMoneda t) {
        return TransaccionMonedaDTO.builder()
                .idTransaccion(t.getIdTransaccion())
                .tipo(t.getTipo())
                .motivo(t.getMotivo())
                .cantidad(t.getCantidad())
                .fecha(t.getFecha())
                .build();
    }
}
