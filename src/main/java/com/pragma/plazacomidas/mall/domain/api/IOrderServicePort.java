package com.pragma.plazacomidas.mall.domain.api;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;

public interface IOrderServicePort {
    OrderModel createOrder(OrderModel orderModel, Long authenticatedClientId);

    List<OrderModel> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size);

    OrderModel assignOrder(Long orderId, Long employeeId, Long employeeRestaurantId);

    OrderModel markOrderAsReady(Long orderId, Long employeeId, Long employeeRestaurantId);

    OrderModel deliverOrder(Long orderId, Long employeeRestaurantId, String securityPin);

    OrderModel cancelOrder(Long orderId, Long authenticatedClientId);
}
