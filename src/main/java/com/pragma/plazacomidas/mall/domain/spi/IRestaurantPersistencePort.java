package com.pragma.plazacomidas.mall.domain.spi;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

public interface IRestaurantPersistencePort {

    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);

    boolean existsById(Long restaurantId);

    RestaurantModel getRestaurantById(Long restaurantId);

    List<RestaurantModel> getAllRestaurants(int page, int size);
}
