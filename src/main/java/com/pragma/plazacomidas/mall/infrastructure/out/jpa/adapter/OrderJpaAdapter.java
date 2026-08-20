package com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.infrastructure.exception.NoDataFoundException;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.OrderItemEntity;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IOrderRepository;

public class OrderJpaAdapter implements IOrderPersistencePort {

    private static final List<String> ACTIVE_STATUS = List.of("PENDIENTE", "EN_PREPARACION", "LISTO");

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    public OrderJpaAdapter(IOrderRepository orderRepository, IOrderEntityMapper orderEntityMapper) {
        this.orderRepository = orderRepository;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);

        for (OrderItemEntity item : orderEntity.getItems()) {
            item.setOrder(orderEntity); //Me toca hacer esto porque el mapper no detecta el order_id y lo dejaría como null
        }

        return orderEntityMapper.toOrderModel(orderRepository.save(orderEntity));
    }

    @Override
    public boolean hasActiveOrder(Long clientId) {
        return orderRepository.existsByClientIdAndStatusIn(clientId, ACTIVE_STATUS);
    }

    @Override
    public List<OrderModel> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return orderEntityMapper.toOrderModelList(
                orderRepository.findByRestaurantIdAndStatus(restaurantId, status, pageRequest).getContent());
    }

    @Override
    public OrderModel getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(orderEntityMapper::toOrderModel).orElseThrow(NoDataFoundException::new);
    }
}
