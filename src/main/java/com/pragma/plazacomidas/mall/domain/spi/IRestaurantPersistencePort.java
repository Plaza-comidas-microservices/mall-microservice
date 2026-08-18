package com.pragma.plazacomidas.mall.domain.spi;

import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;

public interface IRestaurantPersistencePort {

    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);

    boolean existsById(Long restaurantId);

    RestaurantModel getRestaurantById(Long restaurantId);
}
