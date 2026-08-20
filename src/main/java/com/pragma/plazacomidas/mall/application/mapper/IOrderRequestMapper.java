package com.pragma.plazacomidas.mall.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {
    OrderModel toOrder(OrderRequestDto orderRequestDto);
}
