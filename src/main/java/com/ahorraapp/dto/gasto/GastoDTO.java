package com.ahorraapp.dto.gasto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoDTO {

    private Long idGasto;
    private BigDecimal monto;
    private String categoria;
    private String descripcion;
    private String tipoRegistro;
    private String archivoUrl;
    private LocalDateTime fechaRegistro;
}
