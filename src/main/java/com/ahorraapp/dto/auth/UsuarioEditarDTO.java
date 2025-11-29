package com.ahorraapp.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEditarDTO {

    private String nombre;

    private String avatarUrl;

    private String temaActual;
}
