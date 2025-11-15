package com.ahorraapp.service;

import com.ahorraapp.dto.auth.AuthLoginRequestDTO;
import com.ahorraapp.dto.auth.AuthResponseDTO;
import com.ahorraapp.dto.auth.UsuarioRegistroDTO;

public interface AuthService {

    AuthResponseDTO registrar(UsuarioRegistroDTO dto);

    AuthResponseDTO login(AuthLoginRequestDTO dto);
}
