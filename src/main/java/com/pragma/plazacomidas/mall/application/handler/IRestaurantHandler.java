package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.request.RestaurantRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantValidationResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto saveRestaurant(RestaurantRequestDto restaurantRequestDto);

    RestaurantValidationResponseDto getRestaurantById(Long restaurantId);
}
