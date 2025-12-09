package com.ahorraapp.dto.meta;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaCrearDTO {

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotNull
    @Positive(message = "El monto objetivo debe ser positivo.")
    private BigDecimal montoObjetivo;

    @NotNull(message = "Debe especificar una fecha de inicio.")
    private LocalDate fechaInicio; // luego lo conviertes a LocalDate

    @NotNull
    private LocalDate fechaFin;
}
