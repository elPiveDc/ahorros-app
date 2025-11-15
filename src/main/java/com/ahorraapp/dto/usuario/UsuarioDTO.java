package com.ahorraapp.dto.usuario;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private Integer monedas;
    private String avatarUrl;
    private String rol;
    private String temaActual;
    private String avatarEspecial;
}
