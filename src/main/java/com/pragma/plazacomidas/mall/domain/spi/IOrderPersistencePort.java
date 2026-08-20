package com.pragma.plazacomidas.mall.domain.spi;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);

    boolean hasActiveOrder(Long clientId);

    List<OrderModel> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size);

    OrderModel getOrderById(Long orderId);
}
