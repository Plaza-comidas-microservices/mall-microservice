package com.pragma.plazacomidas.mall.application.handler;

import java.util.List;

import com.pragma.plazacomidas.mall.application.dto.request.DeliverOrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderListResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;

public interface IOrderHandler {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto, Long authenticatedClientId);

    List<OrderListResponseDto> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size);

    OrderListResponseDto assignOrder(Long orderId, Long employeeId, Long employeeRestaurantId);

    OrderListResponseDto markOrderAsReady(Long orderId, Long employeeId, Long employeeRestaurantId);

    OrderListResponseDto deliverOrder(Long orderId, Long employeeRestaurantId, DeliverOrderRequestDto deliverOrderRequestDto);

    OrderListResponseDto cancelOrder(Long orderId, Long authenticatedClientId);
}
