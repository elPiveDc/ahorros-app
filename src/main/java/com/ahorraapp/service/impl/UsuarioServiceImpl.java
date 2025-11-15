package com.ahorraapp.service.impl;

import com.ahorraapp.dto.usuario.UsuarioDTO;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO obtenerPerfilActual() {

        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // viene del token

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .monedas(usuario.getMonedas())
                .avatarUrl(usuario.getAvatarUrl())
                .temaActual(usuario.getTemaActual())
                .build();
    }
}
