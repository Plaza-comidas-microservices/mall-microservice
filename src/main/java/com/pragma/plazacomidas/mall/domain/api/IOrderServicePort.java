package com.pragma.plazacomidas.mall.domain.api;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;

public interface IOrderServicePort {
    OrderModel createOrder(OrderModel orderModel, Long authenticatedClientId);
}
