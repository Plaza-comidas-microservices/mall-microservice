package com.pragma.plazacomidas.mall.domain.spi;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);

    boolean hasActiveOrder(Long clientId);
}
