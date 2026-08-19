package com.pragma.plazacomidas.mall.application.handler.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pragma.plazacomidas.mall.application.dto.request.RestaurantRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantValidationResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IRestaurantHandler;
import com.pragma.plazacomidas.mall.application.mapper.IRestaurantRequestMapper;
import com.pragma.plazacomidas.mall.application.mapper.IRestaurantResponseMapper;
import com.pragma.plazacomidas.mall.application.mapper.IRestaurantValidationMapper;
import com.pragma.plazacomidas.mall.domain.api.IRestaurantServicePort;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestaurantValidationMapper restaurantValidationMapper;

    @Override
    public RestaurantResponseDto saveRestaurant(RestaurantRequestDto restaurantRequestDto) {
        RestaurantModel restaurantModel = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        RestaurantModel createdRestaurant = restaurantServicePort.createRestaurant(restaurantModel);
        return restaurantResponseMapper.toResponse(createdRestaurant);
    }

    @Override
    public RestaurantValidationResponseDto getRestaurantById(Long restaurantId) {
        RestaurantModel restaurantModel = restaurantServicePort.getRestaurantById(restaurantId);
        return restaurantValidationMapper.toResponse(restaurantModel);
    }
}
