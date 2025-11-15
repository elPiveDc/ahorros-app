package com.ahorraapp.dto.publicacion;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionCrearDTO {

    @NotBlank
    @Size(min = 3, max = 150)
    private String titulo;

    @NotBlank
    @Size(min = 10, message = "El contenido es demasiado corto.")
    private String contenido;
}
