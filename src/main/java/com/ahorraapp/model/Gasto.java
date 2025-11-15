package com.ahorraapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto")
    private Long idGasto;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String categoria;

    private BigDecimal monto;

    private String descripcion;

    @Column(name = "tipo_registro")
    private String tipoRegistro;

    @Column(name = "archivo_url")
    private String archivoUrl;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
