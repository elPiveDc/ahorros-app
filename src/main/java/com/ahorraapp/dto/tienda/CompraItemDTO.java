package com.ahorraapp.dto.tienda;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraItemDTO {

    private Long idCompra;
    private Integer costoPagado;
    private LocalDateTime fechaCompra;

    private ItemTiendaDTO item;
}