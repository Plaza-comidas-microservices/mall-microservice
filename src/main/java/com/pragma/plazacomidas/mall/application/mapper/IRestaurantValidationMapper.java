package com.pragma.plazacomidas.mall.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.RestaurantValidationResponseDto;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantValidationMapper {
    RestaurantValidationResponseDto toResponse(RestaurantModel restaurantModel);
}
