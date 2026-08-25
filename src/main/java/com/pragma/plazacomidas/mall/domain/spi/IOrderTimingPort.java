package com.pragma.plazacomidas.mall.domain.spi;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.OrderTimingModel;

public interface IOrderTimingPort {

    List<OrderTimingModel> getOrderTimings(List<Long> orderIds);
}
