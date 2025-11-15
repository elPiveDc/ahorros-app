package com.ahorraapp.dto.usuario;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCosmeticoDTO {

    private Long id;
    private Long idCosmetico;
    private String nombreCosmetico;
    private String tipo;
    private String archivoUrl;
    private Boolean aplicado;
}
