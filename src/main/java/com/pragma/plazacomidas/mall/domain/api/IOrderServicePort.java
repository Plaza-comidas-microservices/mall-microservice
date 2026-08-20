package com.pragma.plazacomidas.mall.domain.api;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;

public interface IOrderServicePort {
    OrderModel createOrder(OrderModel orderModel, Long authenticatedClientId);

    List<OrderModel> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size);
}
