package com.ahorraapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_vinculada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVinculada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_app")
    private Long idApp;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_app")
    private String nombreApp;

    private String tipo;

    @Column(name = "token_conexion")
    private String tokenConexion;

    @Column(name = "fecha_vinculacion")
    private LocalDateTime fechaVinculacion;
}
