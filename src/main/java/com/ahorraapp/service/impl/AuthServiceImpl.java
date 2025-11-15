package com.ahorraapp.service.impl;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.security.jwt.JwtUtil;
import com.ahorraapp.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

        private final UsuarioRepository usuarioRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;

        @Override
        public AuthResponseDTO registrar(UsuarioRegistroDTO dto) {

                if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
                        throw new RuntimeException("El correo ya está registrado.");
                }

                Usuario usuario = Usuario.builder()
                                .nombre(dto.getNombre())
                                .correo(dto.getCorreo())
                                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                                .rol("USER")
                                .monedas(0)
                                .avatarUrl(null)
                                .temaActual("default")
                                .build();

                usuarioRepository.save(usuario);

                String token = jwtUtil.generarToken(usuario.getCorreo());

                return AuthResponseDTO.builder()
                                .token(token)
                                .idUsuario(usuario.getIdUsuario())
                                .nombre(usuario.getNombre())
                                .correo(usuario.getCorreo())
                                .rol(usuario.getRol())
                                .monedas(usuario.getMonedas())
                                .avatarUrl(usuario.getAvatarUrl())
                                .temaActual(usuario.getTemaActual())
                                .expiracion(System.currentTimeMillis() + jwtUtil.getExpirationMillis())
                                .build();
        }

        @Override
        public AuthResponseDTO login(AuthLoginRequestDTO dto) {

                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getContrasena()));

                Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

                String token = jwtUtil.generarToken(usuario.getCorreo());

                return AuthResponseDTO.builder()
                                .token(token)
                                .idUsuario(usuario.getIdUsuario())
                                .nombre(usuario.getNombre())
                                .correo(usuario.getCorreo())
                                .rol(usuario.getRol())
                                .monedas(usuario.getMonedas())
                                .avatarUrl(usuario.getAvatarUrl())
                                .temaActual(usuario.getTemaActual())
                                .expiracion(System.currentTimeMillis() + jwtUtil.getExpirationMillis())
                                .build();
        }
}
