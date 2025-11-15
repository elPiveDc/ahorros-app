package com.ahorraapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_cosmetico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCosmetico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_cosmetico")
    private Cosmetico cosmetico;

    private Boolean aplicado;
}
