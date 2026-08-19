package com.pragma.plazacomidas.mall.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.RestaurantListResponseDto;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantListMapper {
    RestaurantListResponseDto toResponse(RestaurantModel restaurantModel);
    List<RestaurantListResponseDto> toResponseList(List<RestaurantModel> restaurantModelList);
}
