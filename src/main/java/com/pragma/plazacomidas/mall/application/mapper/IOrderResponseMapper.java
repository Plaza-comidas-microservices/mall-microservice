package com.pragma.plazacomidas.mall.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderResponseMapper {
    OrderResponseDto toResponse(OrderModel orderModel);
}
