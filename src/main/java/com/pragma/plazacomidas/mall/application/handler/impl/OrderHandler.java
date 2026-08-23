package com.pragma.plazacomidas.mall.application.handler.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pragma.plazacomidas.mall.application.dto.request.DeliverOrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderListResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IOrderHandler;
import com.pragma.plazacomidas.mall.application.mapper.IOrderListMapper;
import com.pragma.plazacomidas.mall.application.mapper.IOrderRequestMapper;
import com.pragma.plazacomidas.mall.application.mapper.IOrderResponseMapper;
import com.pragma.plazacomidas.mall.domain.api.IOrderServicePort;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;
    private final IOrderListMapper orderListMapper;

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderRequestDto, Long authenticatedClientId) {
        OrderModel orderModel = orderRequestMapper.toOrder(orderRequestDto);
        OrderModel createdOrder = orderServicePort.createOrder(orderModel, authenticatedClientId);
        return orderResponseMapper.toResponse(createdOrder);
    }

    @Override
    public List<OrderListResponseDto> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size) {
        List<OrderModel> orders = orderServicePort.getOrdersByRestaurantAndStatus(restaurantId, status, page, size);
        return orderListMapper.toResponseList(orders);
    }

    @Override
    public OrderListResponseDto assignOrder(Long orderId, Long employeeId, Long employeeRestaurantId) {
        OrderModel updatedOrder = orderServicePort.assignOrder(orderId, employeeId, employeeRestaurantId);
        return orderListMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderListResponseDto markOrderAsReady(Long orderId, Long employeeId, Long employeeRestaurantId) {
        OrderModel updatedOrder = orderServicePort.markOrderAsReady(orderId, employeeId, employeeRestaurantId);
        return orderListMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderListResponseDto deliverOrder(Long orderId, Long employeeRestaurantId, DeliverOrderRequestDto deliverOrderRequestDto) {
        OrderModel updatedOrder = orderServicePort.deliverOrder(orderId, employeeRestaurantId, deliverOrderRequestDto.getSecurityPin());
        return orderListMapper.toResponse(updatedOrder);
    }
}
