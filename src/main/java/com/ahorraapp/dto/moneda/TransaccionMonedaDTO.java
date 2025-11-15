package com.ahorraapp.dto.moneda;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionMonedaDTO {

    private Long idTransaccion;
    private String tipo; // ganancia o gasto
    private String motivo;
    private Integer cantidad;
    private LocalDateTime fecha;
}
