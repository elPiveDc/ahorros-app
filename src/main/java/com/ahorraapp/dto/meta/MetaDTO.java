package com.ahorraapp.dto.meta;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaDTO {

    private Long idMeta;
    private String titulo;
    private String descripcion;
    private BigDecimal montoObjetivo;
    private BigDecimal montoActual;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean cumplida;
}
