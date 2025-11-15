package com.ahorraapp.dto.appvinculada;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVinculadaCrearDTO {

    @NotBlank
    private String nombreApp;

    private String tipo;

    @NotBlank
    private String tokenConexion;
}
