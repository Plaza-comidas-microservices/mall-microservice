package com.pragma.plazacomidas.mall.application.handler.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IOrderHandler;
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

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderRequestDto, Long authenticatedClientId) {
        OrderModel orderModel = orderRequestMapper.toOrder(orderRequestDto);
        OrderModel createdOrder = orderServicePort.createOrder(orderModel, authenticatedClientId);
        return orderResponseMapper.toResponse(createdOrder);
    }
}
