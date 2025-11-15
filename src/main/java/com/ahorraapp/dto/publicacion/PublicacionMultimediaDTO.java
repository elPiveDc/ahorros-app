package com.ahorraapp.dto.publicacion;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionMultimediaDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String contenido;

    private MultipartFile imagen;

    private MultipartFile video;
}
