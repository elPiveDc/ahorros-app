package com.ahorraapp.controller;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;
import com.ahorraapp.dto.usuario.UsuarioDTO;
import com.ahorraapp.service.AuthService;
import com.ahorraapp.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registrar(
            @Valid @RequestBody UsuarioRegistroDTO dto) {

        return ResponseEntity.ok(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthLoginRequestDTO dto) {

        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        return ResponseEntity.ok(usuarioService.obtenerPerfilActual());
    }
}
