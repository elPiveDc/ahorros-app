package com.ahorraapp.dto.auth;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginRequestDTO {

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe ser un correo válido.")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena;
}
