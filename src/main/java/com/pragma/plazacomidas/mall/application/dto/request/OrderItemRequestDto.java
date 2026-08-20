package com.pragma.plazacomidas.mall.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {
    private Long plateId;
    private int quantity;
}
