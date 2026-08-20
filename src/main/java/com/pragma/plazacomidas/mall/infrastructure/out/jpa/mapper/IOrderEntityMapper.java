package com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.domain.model.OrderItemModel;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.OrderItemEntity;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {
    OrderEntity toEntity(OrderModel orderModel);
    OrderModel toOrderModel(OrderEntity orderEntity);
    List<OrderItemModel> toOrderItemModelList(List<OrderItemEntity> orderItemEntityList);
    List<OrderModel> toOrderModelList(List<OrderEntity> orderEntityList);
}
