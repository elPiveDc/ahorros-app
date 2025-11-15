package com.ahorraapp.dto.tienda;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTiendaDTO {

    private Long idItem;
    private String nombre;
    private String descripcion;
    private Integer costo;
    private String tipo;
    private String imagenUrl;
}
