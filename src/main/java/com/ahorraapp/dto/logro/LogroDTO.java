package com.ahorraapp.dto.logro;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogroDTO {

    private Long idLogro;
    private String nombre;
    private String descripcion;
    private Integer puntos; // monedas ganadas
    private String fechaLogro;
}
