package com.pragma.plazacomidas.mall.domain.api;

import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

public interface IRestaurantServicePort {

    RestaurantModel createRestaurant(RestaurantModel restaurantModel);

    RestaurantModel getRestaurantById(Long restaurantId);

}
