package com.ahorraapp.dto.tienda;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CosmeticoCrearDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String tipo;

    private String archivoUrl;
}
