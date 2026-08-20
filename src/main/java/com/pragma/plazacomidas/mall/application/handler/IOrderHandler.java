package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;

public interface IOrderHandler {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto, Long authenticatedClientId);
}
