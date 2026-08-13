package com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "active", nullable = false)
    private boolean active;

    // Referencia simple al restaurante dueño del plato, misma BD -> podría ser una
    // relación real @ManyToOne más adelante; se deja como columna plana por ahora,
    // igual que ownerId en RestaurantEntity, para mantenernos en terreno ya conocido.
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;
}
