package com.ahorraapp.service;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponseDTO registrar(UsuarioRegistroDTO dto);

    AuthResponseDTO login(AuthLoginRequestDTO dto);

    AuthResponseDTO obtenerUsuarioActual(HttpServletRequest request);

    void loginYGenerarCookie(AuthLoginRequestDTO dto, HttpServletResponse response);

    void logoutYEliminarCookie(HttpServletResponse response);
}
