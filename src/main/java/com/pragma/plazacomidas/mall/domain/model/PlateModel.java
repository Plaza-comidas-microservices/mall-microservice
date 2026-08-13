package com.pragma.plazacomidas.mall.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlateModel {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String url;
    private String category;
    private boolean active;
    private Long restaurantId;
}
