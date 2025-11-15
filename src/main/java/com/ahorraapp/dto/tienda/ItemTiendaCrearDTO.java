package com.ahorraapp.dto.tienda;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTiendaCrearDTO {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @Positive(message = "El costo debe ser mayor a 0.")
    private Integer costo;

    @NotBlank
    private String tipo; // suscripcion, cosmetico, etc.

    private String imagenUrl;
}
