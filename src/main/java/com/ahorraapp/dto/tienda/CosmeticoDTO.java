package com.ahorraapp.dto.tienda;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CosmeticoDTO {

    private Long idCosmetico;
    private String nombre;
    private String tipo; // tema, avatar, marco, etc.
    private String archivoUrl;
}
