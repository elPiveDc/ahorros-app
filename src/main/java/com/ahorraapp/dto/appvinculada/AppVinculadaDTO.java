package com.ahorraapp.dto.appvinculada;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVinculadaDTO {

    private Long idApp;
    private String nombreApp;
    private String tipo;
    private LocalDateTime fechaVinculacion;
}
