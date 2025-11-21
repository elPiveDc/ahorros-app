package com.ahorraapp.dto.moneda;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionMonedaCrearDTO {

    @NotNull
    private Integer cantidad;

    @NotBlank
    private String motivo;

    @NotBlank
    private String tipo;
}
