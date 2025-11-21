package com.ahorraapp.service.impl;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.security.jwt.JwtUtil;
import com.ahorraapp.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        @Override
        public void loginYGenerarCookie(AuthLoginRequestDTO dto, HttpServletResponse response) {

                AuthResponseDTO loginResponse = login(dto);

                Cookie cookie = new Cookie("AUTH_TOKEN", loginResponse.getToken());
                cookie.setHttpOnly(true);
                cookie.setSecure(false); // HTTPS recomendado (fasle por ahora http)
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60);
                cookie.setAttribute("SameSite", "Lax");

                response.addCookie(cookie);
        }

        @Override
        public void logoutYEliminarCookie(HttpServletResponse response) {

                Cookie cookie = new Cookie("AUTH_TOKEN", null);
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", "Strict");

                response.addCookie(cookie);
        }

        @Override
        public AuthResponseDTO obtenerUsuarioActual(HttpServletRequest request) {

                // 1. Obtener las cookies
                Cookie[] cookies = request.getCookies();
                if (cookies == null) {
                        return null;
                }

                String token = null;

                // 2. Extraer AUTH_TOKEN
                for (Cookie c : cookies) {
                        if ("AUTH_TOKEN".equals(c.getName())) {
                                token = c.getValue();
                                break;
                        }
                }

                if (token == null) {
                        return null;
                }

                // 3. Validar token
                if (!jwtUtil.validarToken(token)) {
                        return null;
                }

                // 4. Extraer correo
                // String correo = jwtUtil.obtenerUsername(token);

                // 5. Obtener usuario en BD
                Usuario usuario = usuarioRepository.findByCorreo("m@gamil.com")
                                .orElse(null);

                if (usuario == null) {
                        return null;
                }

                // 6. Retornar DTO (sin token)
                return AuthResponseDTO.builder()
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
