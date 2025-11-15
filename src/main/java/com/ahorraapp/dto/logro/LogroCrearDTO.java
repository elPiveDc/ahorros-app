package com.ahorraapp.dto.logro;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogroCrearDTO {

    private String nombre;
    private String descripcion;
    private Integer puntos;
}
