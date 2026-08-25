package com.pragma.plazacomidas.mall.domain.usecase;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pragma.plazacomidas.mall.domain.api.IEfficiencyServicePort;
import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.EfficiencyReportModel;
import com.pragma.plazacomidas.mall.domain.model.EmployeeEfficiencyModel;
import com.pragma.plazacomidas.mall.domain.model.OrderEfficiencyModel;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.model.OrderTimingModel;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IOrderTimingPort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

public class EfficiencyUseCase implements IEfficiencyServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IOrderTimingPort orderTimingPort;

    public EfficiencyUseCase(IOrderPersistencePort orderPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
        IOrderTimingPort orderTimingPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.orderTimingPort = orderTimingPort;
    }

    @Override
    public EfficiencyReportModel getEfficiencyReport(Long restaurantId, Long authenticatedOwnerId) {
        if (restaurantId == null) {
            throw new DomainException("El id del restaurante es obligatorio");
        } else if (!restaurantPersistencePort.existsById(restaurantId)) {
            throw new DomainException("El restaurante indicado no existe");
        }

        RestaurantModel restaurant = restaurantPersistencePort.getRestaurantById(restaurantId);
        if (!restaurant.getOwnerId().equals(authenticatedOwnerId)) {
            throw new DomainException("No eres el propietario de este restaurante");
        }

        List<OrderModel> deliveredOrders = orderPersistencePort.getDeliveredOrdersByRestaurant(restaurantId);
        if (deliveredOrders.isEmpty()) {
            return new EfficiencyReportModel(List.of(), List.of());
        }

        List<Long> orderIds = deliveredOrders.stream().map(OrderModel::getId).collect(Collectors.toList());
        Map<Long, OrderTimingModel> timingsByOrderId = orderTimingPort.getOrderTimings(orderIds).stream()
                .collect(Collectors.toMap(OrderTimingModel::getOrderId, timing -> timing));

        List<OrderModel> ordersWithTiming = deliveredOrders.stream()
                .filter(order -> timingsByOrderId.containsKey(order.getId()))
                .collect(Collectors.toList());

        List<OrderEfficiencyModel> orderEfficiencies = ordersWithTiming.stream()
                .map(order -> buildOrderEfficiency(order, timingsByOrderId.get(order.getId())))
                .collect(Collectors.toList());

        List<EmployeeEfficiencyModel> employeeRanking = buildEmployeeRanking(ordersWithTiming, orderEfficiencies);

        return new EfficiencyReportModel(orderEfficiencies, employeeRanking);
    }

    private OrderEfficiencyModel buildOrderEfficiency(OrderModel order, OrderTimingModel timing) {
        long durationSeconds = Duration.between(timing.getStartedAt(), timing.getEndedAt()).getSeconds();
        return new OrderEfficiencyModel(order.getId(), durationSeconds);
    }

    private List<EmployeeEfficiencyModel> buildEmployeeRanking(List<OrderModel> ordersWithTiming, List<OrderEfficiencyModel> orderEfficiencies) {
        Map<Long, Long> durationByOrderId = orderEfficiencies.stream()
                .collect(Collectors.toMap(OrderEfficiencyModel::getOrderId, OrderEfficiencyModel::getDurationSeconds));

        Map<Long, List<Long>> durationsByEmployeeId = ordersWithTiming.stream()
                .filter(order -> order.getAssignedEmployeeId() != null)
                .collect(Collectors.groupingBy(OrderModel::getAssignedEmployeeId,
                        Collectors.mapping(order -> durationByOrderId.get(order.getId()), Collectors.toList())));

        return durationsByEmployeeId.entrySet().stream()
                .map(entry -> new EmployeeEfficiencyModel(entry.getKey(), average(entry.getValue())))
                .sorted(Comparator.comparingDouble(EmployeeEfficiencyModel::getAverageDurationSeconds))
                .collect(Collectors.toList());
    }

    private double average(List<Long> durations) {
        return durations.stream().mapToLong(Long::longValue).average().orElse(0);
    }
}
