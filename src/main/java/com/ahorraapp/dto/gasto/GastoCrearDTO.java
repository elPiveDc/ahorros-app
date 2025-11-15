package com.ahorraapp.dto.gasto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoCrearDTO {

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser positivo.")
    private BigDecimal monto;

    @NotBlank(message = "La categoría es obligatoria.")
    private String categoria;

    private String descripcion;

    @NotBlank
    private String tipoRegistro; // texto, foto, voz

    private String archivoUrl; // opcional
}
