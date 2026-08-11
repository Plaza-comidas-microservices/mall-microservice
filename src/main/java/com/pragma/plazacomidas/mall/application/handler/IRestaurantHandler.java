package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.request.RestaurantRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto saveRestaurant(RestaurantRequestDto restaurantRequestDto);
}
