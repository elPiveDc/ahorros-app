package com.ahorraapp.dto.publicacion;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionDTO {

    private Long idPublicacion;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private Integer likes;
    private String nombreUsuario; // extra útil para la vista
    private String imagenUrl;
    private String videoUrl;

}
