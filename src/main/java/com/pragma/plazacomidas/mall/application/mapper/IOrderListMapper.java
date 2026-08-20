package com.pragma.plazacomidas.mall.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.OrderListResponseDto;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderListMapper {
    OrderListResponseDto toResponse(OrderModel orderModel);
    List<OrderListResponseDto> toResponseList(List<OrderModel> orderModelList);
}
