package com.pragma.plazacomidas.mall.application.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {
    private Long restaurantId;
    private List<OrderItemRequestDto> items;
}
