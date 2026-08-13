package com.pragma.plazacomidas.mall.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlateRequestDto {
    private String name;
    private int price;
    private String description;
    private String url;
    private String category;
    private Long restaurantId;
}
