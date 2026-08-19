package com.pragma.plazacomidas.mall.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlateMenuResponseDto {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String url;
    private String category;
}
