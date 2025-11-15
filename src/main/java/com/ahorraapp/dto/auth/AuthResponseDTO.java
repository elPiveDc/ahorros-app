package com.ahorraapp.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private Long idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private Integer monedas;
    private String avatarUrl;
    private String temaActual;
    private long expiracion; // en milisegundos
}
