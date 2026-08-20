package com.pragma.plazacomidas.mall.application.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long restaurantId;
    private String status;
    private List<OrderItemResponseDto> items;
}
