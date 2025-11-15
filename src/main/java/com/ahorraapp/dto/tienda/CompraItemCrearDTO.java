package com.ahorraapp.dto.tienda;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraItemCrearDTO {

    @NotNull
    private Long idCompra;
    private String nombreItem;
    private String tipoItem;
    private Integer costoPagado;
    private LocalDateTime fechaCompra;
}
