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
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "nit", nullable = false, unique = true, length = 20)
    private String nit;

    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @Column(name = "logo_url", nullable = false, length = 255)
    private String logoUrl;

    // Referencia al id del propietario en users-microservice.
    // No es una FK real de base de datos: cada microservicio tiene su propia BD,
    // así que la validez de este id se confirma vía llamada HTTP, no por integridad referencial.
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
}
