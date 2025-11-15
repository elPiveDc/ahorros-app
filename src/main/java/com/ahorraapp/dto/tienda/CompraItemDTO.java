package com.ahorraapp.dto.tienda;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraItemDTO {
    @NotNull
    private Long idCompra;
}
