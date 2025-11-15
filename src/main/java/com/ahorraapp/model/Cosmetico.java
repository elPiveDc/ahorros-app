package com.ahorraapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cosmetico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cosmetico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cosmetico")
    private Long idCosmetico;

    private String nombre;

    private String tipo;

    @Column(name = "archivo_url")
    private String archivoUrl;
}
