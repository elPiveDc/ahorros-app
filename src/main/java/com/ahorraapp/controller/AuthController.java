package com.ahorraapp.controller;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioEditarDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;
import com.ahorraapp.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registrar(
            @Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.ok(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody AuthLoginRequestDTO dto,
            HttpServletResponse response) {

        authService.loginYGenerarCookie(dto, response);
        return ResponseEntity.ok("Login exitoso");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        authService.logoutYEliminarCookie(response);
        return ResponseEntity.ok("Logout correcto");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {

        AuthResponseDTO dto = authService.obtenerUsuarioActual(request);
        if (dto == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/editar")
    public ResponseEntity<AuthResponseDTO> editarPerfil(
            @Valid @RequestBody UsuarioEditarDTO dto,
            HttpServletRequest request) {

        AuthResponseDTO actualizado = authService.editarPerfil(dto, request);
        return ResponseEntity.ok(actualizado);
    }

}
