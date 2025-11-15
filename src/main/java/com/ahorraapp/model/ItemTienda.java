package com.ahorraapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_tienda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    private String nombre;

    private String descripcion;

    private Integer costo;

    private String tipo;

    @Column(name = "imagen_url")
    private String imagenUrl;
}
